package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.maybe.Maybe;
import be.jdevelopment.tools.validation.maybe.VMonad;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class AddressBuilder extends ValidationProcess {

    AddressBuilder(ObjectProvider provider, FailureBuilder failureBuilder) {
        super(provider, failureBuilder);
    }

    Address build() {
        Address address = new Address();

        addStep(Address.POSTAL_CODE, AddressBuilder::validatePostalCode, address::setPostalCode);
        addStep(Address.STREET, AddressBuilder::validateRequiredString, address::setStreet);
        execute();

        return address;
    }

    private static Maybe<String> validateRequiredString(Object source, FailureBuilder builder) {
        return VMonad.of(source)
                .filter(Objects::nonNull)
                .cut(() -> builder.withCode("required"))
                .filter(String.class::isInstance)
                .cut(() -> builder.withCode("type"))
                .map(String.class::cast);
    }

    private static Pattern POSTAL_CODE_PATTERN = compile("^[0-9]+$");
    private static Maybe<String> validatePostalCode(Object source, FailureBuilder builder) {
        return VMonad.of(source)
                .filter(Objects::nonNull)
                .cut(() -> builder.withCode("required"))
                .filter(String.class::isInstance)
                .cut(() -> builder.withCode("type"))
                .map(String.class::cast)
                .filter(str -> POSTAL_CODE_PATTERN.matcher(str).matches())
                .cut(() -> builder.withCode("format"));
    }

}
