package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.annotations.UnsafeProvider;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.maybe.Maybe;
import be.jdevelopment.tools.validation.maybe.VMonad;
import be.jdevelopment.tools.validation.step.ValidationProcess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Iterator;
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
            String[] cleaned = new String[collection.length];
            for (int i = 0; i < collection.length; i++) {
                int j = i;
                FailureBuilder subBuilder = errorCode -> failureBuilder.withCode(String.format("%s[%d].%s", Person.EMAIL_PROPERTY.getName(), j, errorCode));
                cleaned[i] = validateEmailAddress(collection[i], subBuilder).get();
            }
            person.setEmailAddresses(cleaned);
        });
        addStep(Person.ADDRESS_PROPERTY, PersonBuilder::validateAddress, person::setAddress);
        execute();

        return person;
    }

    private static Maybe<String[]> validateEmailAddressCollection(Object source, FailureBuilder builder) {
        return VMonad.of(source)
                .filter(Objects::nonNull)
                .cut(() -> builder.withCode("required"))
                .filter(Iterator.class::isInstance)
                .map(Iterator.class::cast)
                .map(iterator -> StreamSupport.stream(((Iterable<JsonNode>) () -> iterator).spliterator(), false).collect(Collectors.toList()))
                .map(list -> list.stream().map(JsonNode::asText).toArray(String[]::new))
                .cut(() -> builder.withCode("type"));
    }

    private static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
    private static Maybe<String> validateEmailAddress(Object source, FailureBuilder builder) {
        return VMonad.of(source)
                .filter(Objects::nonNull)
                .cut(() -> builder.withCode("required"))
                .filter(String.class::isInstance)
                .cut(() -> builder.withCode("type"))
                .map(String.class::cast)
                .filter(str -> EMAIL_PATTERN.matcher(str).matches())
                .cut(() -> builder.withCode("format"));
    }

    private static Maybe<Address> validateAddress(Object source, FailureBuilder builder) {
        return VMonad.of(source)
                .filter(Objects::nonNull)
                .cut(() -> builder.withCode("required"))
                .filter(ObjectProvider.class::isInstance)
                .cut(() -> builder.withCode("type"))
                .map(ObjectProvider.class::cast)
                .map(provider -> new AddressBuilder(provider, builder).build());
    }

}
