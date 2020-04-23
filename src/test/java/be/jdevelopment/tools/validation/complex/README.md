# Complex validation example

## Target class

The target class we want to construct in this test is a more complex
version of the `Person` class:
```java
class Person {
    enum PersonProperty implements PropertyToken {
        EMAIL("emailAddresses"),
        ADDRESS("address");

        final String name;
        PersonProperty(String name) {
            this.name = name;
        }

        @Override public String getName() { return name; }
    }

    String[] emailAddresses;
    void setEmailAddresses(Iterator<String> arg) {
        emailAddresses = StreamSupport
                .stream(((Iterable<String>) () -> arg).spliterator(), false)
                .toArray(String[]::new);
    }

    Address address;
    void setAddress(Address arg) { address = arg; }
}
```
The address structure is driven by the following class:
```java
class Address {
    enum AddressProperty implements PropertyToken {
        POSTAL_CODE("postalCode"),
        STREET("street");

        final String name;
        AddressProperty(String name) {
            this.name = name;
        }

        @Override public String getName() { return name; }
    }

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

On `Person` we define a validator for the `ADDRESS_PROPERTY` propertyToken:
```java
private static Property<Address> validateAddress(Object source, MonadOfProperty monad) {
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
`ObjectProvider` for the addres propertyToken. On the sub object providder,
we apply the regular build of an address.

In order to validate the email address collection, we first define a
validator on the collection as a whole:
```java
private static Property<Iterator<String>> validateEmailAddressCollection(Object source, MonadOfProperties monad) {
    return monad.of(source)
            .filter(Objects::nonNull)
            .registerFailureCode("required")
            .filter(String[].class::isInstance)
            .registerFailureCode("type")
            .map(String[].class::cast)
            .map(Arrays::asList)
            .map(List::iterator);
}
```
In this example, we expect that the object provided for the `EMAIL_ADDRESSES`
is an array of String.

## Validation process for `Person`

The whole validation process for person goes as follow:
first we validate the email collection as a whole, and then each collection entry.
This is perform by using the `addCollectionSteps` method:
```java
addCollectionSteps(Person.PersonProperty.EMAIL,
        PersonBuilder::validateEmailAddressCollection,
        PersonBuilder::validateEmailAddress,
        person::setEmailAddresses);
```
Next we validate the address field using our validator function:
```java
addStep(Person.ADDRESS_PROPERTY, PersonBuilder::validateAddress, person::setAddress);
```

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