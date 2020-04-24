package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class PersonBuilder extends ValidationProcess {

    PersonBuilder(ObjectProvider provider, MonadOfProperties monad) {
        super(provider, monad);
    }

    Person build() {
        Person person = new Person();

        addCollectionSteps(Person.PersonProperty.EMAIL,
                PersonBuilder::validateEmailAddressCollection,
                PersonBuilder::validateEmailAddress,
                person::setEmailAddresses);
        addStep(Person.PersonProperty.ADDRESS, PersonBuilder::validateAddress, person::setAddress);
        ValidationRule<Integer> validateDefault = (source, monad) -> monad.of(source)
                .filter(Objects::nonNull)
                .flatMap($ -> monad.of($).filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(Arrays.asList(person.emailAddresses)::indexOf)
                        .filter(x -> x > -1)
                        .registerFailureCode("notfound")
                );
        addStep(Person.PersonProperty.DEFAULT_EMAIL, validateDefault, person::setDefaultEmailIndex);
        execute();

        return person;
    }

    private static Property<Iterator<String>> validateEmailAddressCollection(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String[].class::isInstance)
                .registerFailureCode("type")
                .map(String[].class::cast)
                .map(Arrays::asList)
                .map(List::iterator);
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
