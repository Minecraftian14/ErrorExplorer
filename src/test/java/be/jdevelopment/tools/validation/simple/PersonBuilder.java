package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.InvalidUserInputException;
import be.jdevelopment.tools.validation.error.MonadFactory;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class PersonBuilder extends ValidationProcess {

    PersonBuilder(ObjectProvider provider) {
        super(provider, null);
    }

    private class FailureImpl implements Failure {
        private String errorCode;
        FailureImpl(String errorCode) { this.errorCode = errorCode;}
        @Override public String getCode() { return errorCode; }
    }

    Person build() throws InvalidUserInputException {
        Person person = new Person();
        HashSet<Failure> failures = new HashSet<>();
        this.monad = MonadFactory.on(errorCode -> failures.add(new FailureImpl(errorCode)));

        addStep(Person.EMAIL_PROPERTY_TOKEN, PersonBuilder::validateEmailAddress, person::setEmailAddress)
                .execute();

        if (!failures.isEmpty()) {
            throw new InvalidUserInputException(failures);
        }

        return person;
    }

    private static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
    private static Property<String> validateEmailAddress(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode("type")
                .map(String.class::cast)
                .filter(str -> EMAIL_PATTERN.matcher(str).matches())
                .registerFailureCode("format");
    }
}
