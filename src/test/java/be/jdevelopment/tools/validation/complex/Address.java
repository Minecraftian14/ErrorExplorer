package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.Property;

class Address {
    static final Property POSTAL_CODE = () -> "postalCode";
    static final Property STREET = () -> "street";

    String street, postalCode;
    void setStreet(String arg) { street = arg; }
    void setPostalCode(String arg) { postalCode = arg; }
}
