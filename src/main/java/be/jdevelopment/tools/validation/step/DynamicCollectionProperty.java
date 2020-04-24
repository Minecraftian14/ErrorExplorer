package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.PropertyToken;

class DynamicCollectionProperty implements PropertyToken {
    private final String name;
    final int index;
    DynamicCollectionProperty(int i, PropertyToken baseToken) {
        name = String.format("%s[%d]", baseToken.getName(), i);
        index = i;
    }

    @Override public String getName() { return name; }

}
