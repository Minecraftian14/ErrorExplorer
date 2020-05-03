package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class AddressFactory {

    private final MonadOfProperties monad;
    AddressFactory(MonadOfProperties monad) {
        this.monad = monad;
    }

    private static class MutAddress {
        String street, postalCode;

        void setStreet(String arg) { street = arg; }
        void setPostalCode(String arg) { postalCode = arg; }
    }

    Address create(ObjectProvider provider) {
        MutAddress mutAddress = new MutAddress();

        new ValidationProcess(provider, monad)
            .addStep(AddressProperty.STREET, AddressFactory::validateRequiredString, mutAddress::setStreet)
            .addStep(AddressProperty.POSTAL_CODE, AddressFactory::validatePostalCode, mutAddress::setPostalCode);

        return new Address(mutAddress.street, mutAddress.postalCode);
    }

    private static Property<String> validateRequiredString(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode("type")
                .map(String.class::cast);
    }

    private final static Pattern POSTAL_CODE_PATTERN = compile("^[0-9]+$");
    private static Property<String> validatePostalCode(Object source, MonadOfProperties monad) {
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
