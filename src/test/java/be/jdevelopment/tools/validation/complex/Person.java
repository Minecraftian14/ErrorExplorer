package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.PropertyToken;

class Person {
    static final PropertyToken EMAIL_PROPERTY_TOKEN = () -> "emailAddresses";
    static final PropertyToken ADDRESS_PROPERTY_TOKEN = () -> "address";

    String[] emailAddresses;
    void setEmailAddresses(String[] arg) {
        emailAddresses = arg;
    }

    Address address;
    void setAddress(Address arg) { address = arg; }
}
