package be.jdevelopment.tools.validation.error;

@FunctionalInterface
public interface FailureBuilder {

    void withCode(String errorCode);

    default void withFailure(Failure failure) {
        withCode(failure.getCode());
    }

}
