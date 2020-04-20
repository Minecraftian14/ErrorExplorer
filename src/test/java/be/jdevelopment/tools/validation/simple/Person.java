package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.PropertyToken;

class Person {
    static final PropertyToken EMAIL_PROPERTY_TOKEN = () -> "emailAddress";

    String emailAddress;
    void setEmailAddress(String arg) { emailAddress = arg; }
}
