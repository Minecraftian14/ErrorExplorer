package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.PropertyToken;

import java.util.Iterator;
import java.util.stream.StreamSupport;

class Person {
    static enum PersonProperty implements PropertyToken {
        EMAIL("emailAddresses"),
        ADDRESS("address");

        final String name;
        PersonProperty(String name) {
            this.name = name;
        }

        @Override public String getName() { return name; }
    }

    String[] emailAddresses;
    void setEmailAddresses(Iterator<String> arg) {
        emailAddresses = StreamSupport
                .stream(((Iterable<String>) () -> arg).spliterator(), false)
                .toArray(String[]::new);
    }

    Address address;
    void setAddress(Address arg) { address = arg; }
}
