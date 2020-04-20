package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.error.MonadFactory;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;

import java.util.HashMap;
import java.util.Map;

public class ValidationProcess {

    private final ObjectProvider provider;
    protected MonadOfProperties monad;
    private Map<PropertyToken, ValidationRule> scriptMapping = new HashMap<>();

    public ValidationProcess(ObjectProvider provider, MonadOfProperties monad) {
        this.monad = monad;
        this.provider = provider;
    }

    private static MonadOfProperties deriveFromParent(PropertyToken propertyToken, MonadOfProperties baseMonad) {
        return MonadFactory.on(errorCode -> baseMonad.fail().registerFailureCode(String.format("%s.%s", propertyToken.getName(), errorCode)));
    }

    public <T> ValidationProcess addStep(PropertyToken propertyToken, ValidationRule<T> rule, Callback<T> andThen) {
        ValidationCommand<T> cmd = new ValidationCommand<>();
        cmd.rule = rule;
        cmd.callback = andThen;

        scriptMapping.put(propertyToken, cmd);
        return this;
    }

    public void execute() {
        MonadOfProperties subMonad;
        PropertyToken propertyToken;
        ValidationRule rule;
        Object source;
        for (Map.Entry<PropertyToken, ValidationRule> entry : scriptMapping.entrySet()) {
            propertyToken = entry.getKey();
            rule = entry.getValue();
            subMonad = deriveFromParent(propertyToken, monad);
            source = provider.provideFor(propertyToken);
            rule.validate(source, subMonad);
        }
    }

    @FunctionalInterface
    public interface ValidationRule<T> {
        Property<T> validate(Object source, MonadOfProperties workingMonad);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T arg);
    }

    private static class ValidationCommand<T> implements ValidationRule<T> {
        ValidationRule<T> rule;
        Callback<T> callback;

        @Override
        public Property<T> validate(Object source, MonadOfProperties b) {
            return rule.validate(source, b).map(this::peek);
        }

        private T peek(T arg) {
            callback.call(arg);
            return arg;
        }
    }

}
