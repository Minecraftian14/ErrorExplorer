package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class AddressBuilder extends ValidationProcess {

    AddressBuilder(ObjectProvider provider, MonadOfProperties monad) {
        super(provider, monad);
    }

    Address build() {
        Address address = new Address();

        addStep(Address.AddressProperty.STREET, AddressBuilder::validateRequiredString, address::setStreet);
        addStep(Address.AddressProperty.POSTAL_CODE, AddressBuilder::validatePostalCode, address::setPostalCode);

        return address;
    }

    private static Property<String> validateRequiredString(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode("type")
                .map(String.class::cast);
    }

    private static Pattern POSTAL_CODE_PATTERN = compile("^[0-9]+$");
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
