package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.Property;
import be.jdevelopment.tools.validation.annotations.UnsafeProvider;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.maybe.Maybe;
import be.jdevelopment.tools.validation.error.VMonad;
import be.jdevelopment.tools.validation.step.ValidationProcess;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.regex.Pattern.compile;

class PersonBuilder extends ValidationProcess {

    PersonBuilder(@UnsafeProvider(expect = Person.class) ObjectProvider provider, FailureBuilder failureBuilder) {
        super(provider, failureBuilder);
    }

    Person build() {
        Person person = new Person();

        addStep(Person.EMAIL_PROPERTY, PersonBuilder::validateEmailAddressCollection, collection -> {
            List<String> emailAddresses = new ArrayList<>();
            for (int i = 0; i < collection.length; i++) {
                int j = i;
                Property property = () -> String.format("%s[%d]", Person.EMAIL_PROPERTY.getName(), j);
                new ValidationProcess($ -> collection[j], failureBuilder)
                        .addStep(property, PersonBuilder::validateEmailAddress, emailAddresses::add)
                        .execute();
            }
            person.setEmailAddresses(emailAddresses.toArray(new String[0]));
        });
        addStep(Person.ADDRESS_PROPERTY, PersonBuilder::validateAddress, person::setAddress);
        execute();

        return person;
    }

    private static Maybe<String[]> validateEmailAddressCollection(Object source, FailureBuilder builder) {
        return VMonad.of(source, builder)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(Iterator.class::isInstance)
                .map(Iterator.class::cast)
                .map(iterator -> StreamSupport.stream(((Iterable<JsonNode>) () -> iterator).spliterator(), false).collect(Collectors.toList()))
                .map(list -> list.stream().map(JsonNode::asText).toArray(String[]::new))
                .registerFailureCode("type");
    }

    private static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
    private static Maybe<String> validateEmailAddress(Object source, FailureBuilder builder) {
        return VMonad.of(source, builder)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode(("type"))
                .map(String.class::cast)
                .filter(str -> EMAIL_PATTERN.matcher(str).matches())
                .registerFailureCode("format");
    }

    private static Maybe<Address> validateAddress(Object source, FailureBuilder builder) {
        return VMonad.of(source, builder)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(ObjectProvider.class::isInstance)
                .registerFailureCode("type")
                .map(ObjectProvider.class::cast)
                .map(provider -> new AddressBuilder(provider, builder).build());
    }

}
