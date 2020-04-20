package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.PropertyToken;

class Person {
    enum PersonProperty implements PropertyToken {
        EMAIL("emailAddresses"),
        ADDRESS("address");

        final String name;
        PersonProperty(String name) {
            this.name = name;
        }

        @Override public String getName() { return name; }
    }

    String[] emailAddresses;
    void setEmailAddresses(String[] arg) {
        emailAddresses = arg;
    }

    Address address;
    void setAddress(Address arg) { address = arg; }
}
