package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.Property;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.maybe.Maybe;

import java.util.HashMap;
import java.util.Map;

public class ValidationProcess {

    protected final ObjectProvider provider;
    protected final FailureBuilder failureBuilder;
    private Map<Property, ValidationRule> scriptMapping = new HashMap<>();

    public ValidationProcess(ObjectProvider provider, FailureBuilder failureBuilder) {
        this.provider = provider;
        this.failureBuilder = failureBuilder;
    }

    private static FailureBuilder deriveFromParent(Property property, FailureBuilder parentBuilder) {
        return errorCode -> parentBuilder.withCode(String.format("%s.%s", property.getName(), errorCode));
    }

    public <T> ValidationProcess addStep(Property property, ValidationRule<T> rule, Callback<T> andThen) {
        ValidationCommand<T> cmd = new ValidationCommand<>();
        cmd.rule = rule;
        cmd.callback = andThen;

        scriptMapping.put(property, cmd);
        return this;
    }

    public void execute() {
        FailureBuilder subBuilder;
        Property property;
        ValidationRule rule;
        Object source;
        for (Map.Entry<Property, ValidationRule> entry : scriptMapping.entrySet()) {
            property = entry.getKey();
            rule = entry.getValue();
            subBuilder = deriveFromParent(property, failureBuilder);
            source = provider.provideForm(property);
            rule.validate(source, subBuilder);
        }
    }

    @FunctionalInterface
    public interface ValidationRule<T> {
        Maybe<T> validate(Object source, FailureBuilder failureBuilder);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T arg);
    }

    private static class ValidationCommand<T> implements ValidationRule<T> {
        ValidationRule<T> rule;
        Callback<T> callback;

        @Override
        public Maybe<T> validate(Object source, FailureBuilder b) {
            return rule.validate(source, b).map(this::peek);
        }

        private T peek(T arg) {
            callback.call(arg);
            return arg;
        }
    }

}
