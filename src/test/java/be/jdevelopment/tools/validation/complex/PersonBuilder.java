package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class PersonBuilder extends ValidationProcess {

    PersonBuilder(ObjectProvider provider, MonadOfProperties monad) {
        super(provider, monad);
    }

    Person build() {
        Person person = new Person();

        addStep(Person.EMAIL_PROPERTY_TOKEN, PersonBuilder::validateEmailAddressCollection, collection -> {
            List<String> emailAddresses = new ArrayList<>();
            for (int i = 0; i < collection.length; i++) {
                int j = i;
                PropertyToken propertyToken = () -> String.format("%s[%d]", Person.EMAIL_PROPERTY_TOKEN.getName(), j);
                new ValidationProcess($ -> collection[j], monad)
                        .addStep(propertyToken, PersonBuilder::validateEmailAddress, emailAddresses::add)
                        .execute();
            }
            person.setEmailAddresses(emailAddresses.toArray(new String[0]));
        });
        addStep(Person.ADDRESS_PROPERTY_TOKEN, PersonBuilder::validateAddress, person::setAddress);
        execute();

        return person;
    }

    private static Property<String[]> validateEmailAddressCollection(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String[].class::isInstance)
                .registerFailureCode("type")
                .map(String[].class::cast);
    }

    private static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
    private static Property<String> validateEmailAddress(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode(("type"))
                .map(String.class::cast)
                .filter(str -> EMAIL_PATTERN.matcher(str).matches())
                .registerFailureCode("format");
    }

    private static Property<Address> validateAddress(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(ObjectProvider.class::isInstance)
                .registerFailureCode("type")
                .map(ObjectProvider.class::cast)
                .map(provider -> new AddressBuilder(provider, monad).build());
    }

}
