package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.Property;

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
