package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.PropertyToken;

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
