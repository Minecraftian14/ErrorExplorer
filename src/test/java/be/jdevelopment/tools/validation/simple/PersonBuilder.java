package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.annotations.UnsafeProvider;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.maybe.Maybe;
import be.jdevelopment.tools.validation.error.VMonad;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class PersonBuilder extends ValidationProcess {

    PersonBuilder(@UnsafeProvider(expect = Person.class) ObjectProvider provider, FailureBuilder builder) {
        super(provider, builder);
    }

    Person build() {
        Person person = new Person();

        addStep(Person.EMAIL_PROPERTY, PersonBuilder::validateEmailAddress, person::setEmailAddress)
                .execute();

        return person;
    }

    private static Pattern EMAIL_PATTERN = compile("^[a-zA-Z0-9.\\-_]+@[a-zA-Z0-9.\\-_]+$");
    private static Maybe<String> validateEmailAddress(Object source, FailureBuilder builder) {
        return VMonad.of(source, builder)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode("type")
                .map(String.class::cast)
                .filter(str -> EMAIL_PATTERN.matcher(str).matches())
                .registerFailureCode("format");
    }
}
