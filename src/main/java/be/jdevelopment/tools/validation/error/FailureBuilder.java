package be.jdevelopment.tools.validation.error;

import java.util.HashMap;
import java.util.HashSet;

@FunctionalInterface
public interface FailureBuilder {

    void withCode(String errorCode, String message);

    default void withFailure(Failure failure, String message) {
        withCode(failure.getCode(), message);
    }

    static FailureBuilder getDefault(HashSet<Failure> failures) {
        return  (errorCode, message) -> failures.add(() ->
                errorCode + ((message != null && !message.strip().equals("")) ? (": " + message) : "")
        );
    }

}
