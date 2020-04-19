# Complex validation example

## Target class

The target class we want to construct in this test is a more complex
version of the `Person` class:
```java
class Person {
    static final Property EMAIL_PROPERTY = () -> "emailAddresses";
    static final Property ADDRESS_PROPERTY = () -> "address";

    String[] emailAddresses;
    void setEmailAddresses(String[] arg) {
        emailAddresses = arg;
    }

    Address address;
    void setAddress(Address arg) { address = arg; }
}
```
The address structure is driven by the following class:
```java
class Address {
    static final Property POSTAL_CODE = () -> "postalCode";
    static final Property STREET = () -> "street";

    String street, postalCode;
    void setStreet(String arg) { street = arg; }
    void setPostalCode(String arg) { postalCode = arg; }
}
```
There are more things to validate: the validity of the email addresses,
and the validity of the postal address (postal code and street).

## Validation method

As in the simple case, we define basic validators. The validators on
`Address` are simple and follow the same scheme than the ones in the
simple validation example.

On `Person` we define a validator for the `ADDRESS_PROPERTY` property:
```java
private static Maybe<Address> validateAddress(Object source, MaybeMonad monad) {
    return monad.of(source)
            .filter(Objects::nonNull)
            .registerFailureCode("required")
            .filter(ObjectProvider.class::isInstance)
            .registerFailureCode("type")
            .map(ObjectProvider.class::cast)
            .map(provider -> new AddressBuilder(provider, builder).build());
}
```
We expect that the `ObjectProvider` for `Person` can provide another
`ObjectProvider` for the addres property. On the sub object providder,
we apply the regular build of an address.

In order to validate the email address collection, we first define a
validator on the collection as a whole:
```java
private static Maybe<String[]> validateEmailAddressCollection(Object source, MaybeMonad monad) {
    return monad.of(source)
            .filter(Objects::nonNull)
            .registerFailureCode("required")
            .filter(String[].class::isInstance)
            .registerFailureCode("type")
            .map(String[].class::cast);
}
```
In this example, we expect that the object provided for the `EMAIL_ADDRESSES`
is an array of String.

## Validation process for `Person`

The whole validation process for person goes as follow:
first we validate the email collection as a whole, and in the callback,
we are going to create validation processes for each
entry of the array.
```java
addStep(Person.EMAIL_PROPERTY, PersonBuilder::validateEmailAddressCollection, collection -> {
    List<String> emailAddresses = new ArrayList<>();
    for (int i = 0; i < collection.length; i++) {
        int j = i;
        Property property = () -> String.format("%s[%d]", Person.EMAIL_PROPERTY.getName(), j);
        new ValidationProcess($ -> collection[j], failureBuilder)
                .addStep(property, PersonBuilder::validateEmailAddress, emailAddresses::add)
                .execute();
    }
    person.setEmailAddresses(emailAddresses.toArray(new String[0]));
});
```
Next we validate the address field using our validator function:
```java
addStep(Person.ADDRESS_PROPERTY, PersonBuilder::validateAddress, person::setAddress);
```

### Why so much boiler plate for a collection?

Basically because the error code logic is not based on the object hierarchy,
but based on constraints. The tree of constraints in an object follows the
field hierarchy, and each leaf is represents a invalid constraint.

On a collection, there are two possible perspectives one can adopt:
either entry 0 of the collection is a constraint on the collection;
or the entry 0 on the collection is a constraint on the collection container.

The first position would naturally yield error code of the form
```json
collectionContainer.collection.0.errorCode
```
Although it may be ok, we have prefered (in this example at least) to see
a collection entry as a constraint on the collection container:
it was stored on a collection because we only know at runtime how many
constraints of type `collection` exist on the container.

From this perspective, it's more natural to print an error code of the form
```json
collectionContainer.collection[0].errorCode
```
as the real constraint is `collection[0]`. Never mind! Prefer the first
form if you are puzzled with this.

## Example

A mock of the form
```json
{
  "emailAddresses": ["not_a_mail_address"],
  "address": {
    "postalCode": "woops",
    "street": "This is ok"
  }
}
```
will give an error collection of the form
```
[ "emailAddresses[0].format" , "address.postalCode.format" ]
```