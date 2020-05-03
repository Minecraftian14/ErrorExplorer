package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.InvalidUserInputException;
import be.jdevelopment.tools.validation.error.MonadFactory;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class PersonFactory {

    private static class FailureImpl implements Failure {
        private String errorCode;
        FailureImpl(String errorCode) { this.errorCode = errorCode;}
        @Override public String getCode() { return errorCode; }
    }

    private static class MutPerson {
        String emailAddress;

        void setEmailAddress(String arg) { emailAddress = arg; }

        Person toImmutable() {
            return new Person(emailAddress);
        }
    }

    static Person create(ObjectProvider provider) throws InvalidUserInputException {
        MutPerson mutPerson = new MutPerson();
        HashSet<Failure> failures = new HashSet<>();
        ValidationProcess process = new ValidationProcess(provider,
                MonadFactory.on(errorCode -> failures.add(new FailureImpl(errorCode)))
        );

        process.addStep(PersonProperty.EMAIL, PersonFactory::validateEmailAddress, mutPerson::setEmailAddress);

        if (!failures.isEmpty()) {
            throw new InvalidUserInputException(failures);
        }

        return mutPerson.toImmutable();
    }

    private final static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
    static Property<String> validateEmailAddress(Object source, MonadOfProperties monad) {
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
