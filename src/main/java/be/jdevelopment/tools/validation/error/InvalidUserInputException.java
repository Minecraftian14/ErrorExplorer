package be.jdevelopment.tools.validation.error;

import java.util.Collection;
import java.util.HashSet;

public class InvalidUserInputException extends Exception {

    private final HashSet<Failure> failures;

    public InvalidUserInputException(Collection<Failure> failures) {
        this.failures = new HashSet<>(failures);
    }

    public Collection<Failure> getFailures() {
        return failures;
    }

}
