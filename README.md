# Java object validation

The aim of this little projet is to propose a simple process to validate an object
input from a client, and use the validation result to build a complex object.

## Example

Let's say you have a structure of the form
```json
{
  "emailAddresses": ["john.doe@universe.com", "private@home"],
  "address": {
    "postalCode": "5030",
    "street": "Some street right after the corner"
  }
}
```
You expect to map this structure on a class of the form
```java
class Person {
    String[] emailAddresses;
    Address address;
}
class Address {
    String postalCode;
    String street;
}
```
In order to do the mapping, you usually rely on external libraries as Jackson.
This forces you to first parse the json input, and *then validate it*.

The aim of the current project is to provide facilities to validate and build
objects at the same time, with as less effort as possible.
```