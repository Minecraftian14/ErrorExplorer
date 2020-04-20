package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.PropertyToken;

class Address {
    static final PropertyToken POSTAL_CODE = () -> "postalCode";
    static final PropertyToken STREET = () -> "street";

    String street, postalCode;
    void setStreet(String arg) { street = arg; }
    void setPostalCode(String arg) { postalCode = arg; }
}
