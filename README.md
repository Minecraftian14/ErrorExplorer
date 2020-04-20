# Java object validation

The aim of this little projet is to propose a simple process to validate an object
input from a client, and use the validation result to build a complex object.

## Motivation

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

## Project architecture: the basics

### The `Failure` error

Validation errors are encoded in a `Failure` object.
A `Failure` has a code, which correspond to the reason why the failure occurred.

### The `Maybe` monad

A user input may be, or may not be. This phenomenon is enclosed within
the `Maybe<T>` monad.

This monad offers very few computation facilities. Its aim is to enclose
user input, and validate them via the `filter` method. Once an input validated,
the monad allow you to `flatMap` or `map` it on another type, which
may be or may not be.

It's really like the `java.util.Optional`, although there are no integration
with streams. Rather, a `Maybe` should implement the `registerErrorCode`
method, which allow one to register an error code.

### `Property` and `ObjectProvider`

The user inputs are assumed to be encoded as an `ObjectProvider` instance.
(See unit tests for an example of `ObjectProvider` construction based on
Json.)

The `ObjectProvider` provides an object on the basis of a `Property`:
```
interface ObjectProvider {
   Object provideFor(Property propertyToken);
}
```
A `Property` is as elementary as this:
```
interface Property {
   String getName();
}
```
Although the provision of an object will be done on the basis of the
propertyToken name, this is not required at all.

### The `ValidationProcess` mechanism

A `ValidationProcess` offers you the availability to encode different
constraints on an object. In order to define a `ValidationProcess`, you
need to give it its execution context: an `ObjectProvider` and a `FailureBuilder`.

Once a `ValidationProcess` defined,
you can add it *validation steps*:
```
addStep( Property , Object -> Maybe<T> , T -> void );
```
The first argument is the propertyToken that will be validated.

The second argument is the extraction process. During this stage, you'll
map the user input to some `Maybe` resource, and advertize the
`FailureBuilder` about failures using the `registerFailureCode` method of
the `Maybe` interface.

The third parameter is a call back on what should be done with the *valide*
input. It will not be executed if the input is invalid.

**Note:** Adding sets is a stack operation. To execute the step, you'll need to
call the `execute` method.

## After words

Learn more about usage by visiting the unit tests section. We put great
love in it <3