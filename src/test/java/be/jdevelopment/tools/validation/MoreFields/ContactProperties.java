package be.jdevelopment.tools.validation.MoreFields;

import be.jdevelopment.tools.validation.PropertyToken;

enum  ContactProperties implements PropertyToken {
    NAME("name"),
    NUMBER("number"),
    DEBT("debt");

    final String value;

    ContactProperties(String name) {
        this.value = name;
    }

    @Override
    public String getName() {
        return value;
    }
}
