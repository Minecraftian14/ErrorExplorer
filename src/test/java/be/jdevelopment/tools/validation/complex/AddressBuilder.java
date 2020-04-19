package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.maybe.Maybe;
import be.jdevelopment.tools.validation.maybe.MaybeMonad;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class AddressBuilder extends ValidationProcess {

    AddressBuilder(ObjectProvider provider, MaybeMonad monad) {
        super(provider, monad);
    }

    Address build() {
        Address address = new Address();

        addStep(Address.STREET, AddressBuilder::validateRequiredString, address::setStreet);
        addStep(Address.POSTAL_CODE, AddressBuilder::validatePostalCode, address::setPostalCode);
        execute();

        return address;
    }

    private static Maybe<String> validateRequiredString(Object source, MaybeMonad monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode("type")
                .map(String.class::cast);
    }

    private static Pattern POSTAL_CODE_PATTERN = compile("^[0-9]+$");
    private static Maybe<String> validatePostalCode(Object source, MaybeMonad monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode("type")
                .map(String.class::cast)
                .filter(str -> POSTAL_CODE_PATTERN.matcher(str).matches())
                .registerFailureCode("format");
    }

}
