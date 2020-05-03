package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.PropertyToken;

enum PersonProperty implements PropertyToken {
    EMAIL("emailAddresses"),
    DEFAULT_EMAIL("defaultEmail"),
    ADDRESS("address");

    final String name;

    PersonProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}