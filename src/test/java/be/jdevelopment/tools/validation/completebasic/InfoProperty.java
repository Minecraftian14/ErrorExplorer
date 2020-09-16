package be.jdevelopment.tools.validation.completebasic;

import be.jdevelopment.tools.validation.PropertyToken;

enum  InfoProperty implements PropertyToken {
    DATA("data");

    final String name;

    InfoProperty(String name) {this.name = name;}

    public String getName() {
        return name;
    }
}
