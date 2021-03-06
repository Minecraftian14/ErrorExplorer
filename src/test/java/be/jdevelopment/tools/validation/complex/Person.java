package be.jdevelopment.tools.validation.complex;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

record Person(MailCollection mails, Address address) {

    /** Shortcut to allow easier access */
    Collection<String> getAllMails() {
        return Optional.ofNullable(mails())
                .map(MailCollection::getMails)
                .stream()
                .flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false))
                .collect(Collectors.toList());
    }

}
