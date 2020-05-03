package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.PropertyToken;

enum PersonProperty implements PropertyToken {
    EMAIL("emailAddress");

    private final String name;
    PersonProperty(String name) {
        this.name = name;
    }

    @Override public String getName() { return name; }
}
