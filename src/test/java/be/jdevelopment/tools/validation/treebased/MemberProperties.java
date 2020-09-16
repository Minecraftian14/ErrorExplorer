package be.jdevelopment.tools.validation.treebased;

import be.jdevelopment.tools.validation.PropertyToken;

public enum MemberProperties implements PropertyToken {
    NAME("name"),
    UNDERLINGS("underlings");

    final String value;

    MemberProperties(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return value;
    }
}
