package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.PropertyToken;

class Person {
    enum PersonProperty implements PropertyToken {
        EMAIL("emailAddress");

        final String name;
        PersonProperty(String name) {
            this.name = name;
        }

        @Override public String getName() { return name; }
    }

    String emailAddress;
    void setEmailAddress(String arg) { emailAddress = arg; }
}
