# Simple validation example

## Target class

The target class we want to construct in this test is a simple
version of the `Person` class:
```java
class Person {
    static final Property EMAIL_PROPERTY = () -> "emailAddress";

    String emailAddress;
    void setEmailAddress(String arg) { emailAddress = arg; }
}
```
The only field to extract from the user inputs is the email address
property, whose name is `emailAddress`. We have defined a setter method to
allow the build of a `Person`. (Getter could also be set, but this is out of
scope.)

## Validation method

We first define a generic validation method to validate an email address.
Method is put in the `PersonValidationBuilder` class for this example,
but you could have put it in some shared package:
```java
private static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
private static Maybe<String> validateEmailAddress(Object source, FailureBuilder builder) {
    return VMonad.of(source, builder)
            .filter(Objects::nonNull)
            .registerFailureCode("required")
            .filter(String.class::isInstance)
            .registerFailureCode("type")
            .map(String.class::cast)
            .filter(str -> EMAIL_PATTERN.matcher(str).matches())
            .registerFailureCode("format");
}
```
We first define a monad structure to use the `Maybe` algebra,
and we directly wrap the `source` is a `Maybe` object.
(Checkout the code of `VMonad` if you're interested in implementation details.)

The remaining part of the code is a validation process.
Each time we encounter an error, we register the error code. Registration is
implicitly done within the `FailureBuilder`.

## Validation process

The whole validation process for a `Person` is expressed by extending the
`ValidationProcess` class. This is the `PersonBuilder` class.

The `ValidationProcess` provides basic methods to build a `Person`, so
we just need to write the whole build method (which is independent of the
`ValidationProcess` structure, so no override is done here):
```
Person build() throws InvalidUserInputException {
    Person person = new Person();
    HashSet<Failure> failures = new HashSet<>();
    this.failureBuilder = errorCode -> failures.add(new FailureImpl(errorCode));

    addStep(Person.EMAIL_PROPERTY, PersonBuilder::validateEmailAddress, person::setEmailAddress)
            .execute();

    if (!failures.isEmpty()) {
        throw new InvalidUserInputException(failures);
    }

    return person;
}
```

## Example

As an example, a mock of the form
```json
{
  "emailAddress": null
}
```
ends with the error collection
```
[ "emailAddress.required" ]
```