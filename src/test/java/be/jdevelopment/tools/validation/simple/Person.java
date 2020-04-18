package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.Property;

class Person {
    static final Property EMAIL_PROPERTY = () -> "emailAddress";

    String emailAddress;
    void setEmailAddress(String arg) { emailAddress = arg; }
}
