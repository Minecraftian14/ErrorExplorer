package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.PropertyToken;

import java.util.Iterator;
import java.util.stream.StreamSupport;

class Person {
    enum PersonProperty implements PropertyToken {
        EMAIL("emailAddresses"),
        DEFAULT_EMAIL("defaultEmail"),
        ADDRESS("address");

        final String name;
        PersonProperty(String name) {
            this.name = name;
        }

        @Override public String getName() { return name; }
    }

    int defaultEmailIndex;
    String[] emailAddresses;
    void setEmailAddresses(Iterator<String> arg) {
        emailAddresses = StreamSupport
                .stream(((Iterable<String>) () -> arg).spliterator(), false)
                .toArray(String[]::new);
    }
    void setDefaultEmailIndex(int index) {
        defaultEmailIndex = index;
    }

    Address address;
    void setAddress(Address arg) { address = arg; }
}
