package be.jdevelopment.tools.validation.error;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class InvalidUserInputException extends Exception {

    private final HashSet<Failure> failures;

    public InvalidUserInputException(Collection<Failure> failures) {
        this.failures = new HashSet<>(failures);
    }
    
    public InvalidUserInputException(String unexpectedErrorMessage) {
    	this.failures = new HashSet(List.<Failure> of(unexpectedErrorMessage::toString));
    }

    public Collection<Failure> getFailures() {
        return failures;
    }

}
