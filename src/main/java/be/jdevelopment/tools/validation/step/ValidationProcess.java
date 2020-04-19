package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.Property;
import be.jdevelopment.tools.validation.error.VMonad;
import be.jdevelopment.tools.validation.maybe.Maybe;
import be.jdevelopment.tools.validation.maybe.MaybeMonad;

import java.util.HashMap;
import java.util.Map;

public class ValidationProcess {

    private final ObjectProvider provider;
    protected MaybeMonad monad;
    private Map<Property, ValidationRule> scriptMapping = new HashMap<>();

    public ValidationProcess(ObjectProvider provider, MaybeMonad monad) {
        this.monad = monad;
        this.provider = provider;
    }

    private static MaybeMonad deriveFromParent(Property property, MaybeMonad baseMonad) {
        return VMonad.on(errorCode -> baseMonad.fail().registerFailureCode(String.format("%s.%s", property.getName(), errorCode)));
    }

    public <T> ValidationProcess addStep(Property property, ValidationRule<T> rule, Callback<T> andThen) {
        ValidationCommand<T> cmd = new ValidationCommand<>();
        cmd.rule = rule;
        cmd.callback = andThen;

        scriptMapping.put(property, cmd);
        return this;
    }

    public void execute() {
        MaybeMonad subMonad;
        Property property;
        ValidationRule rule;
        Object source;
        for (Map.Entry<Property, ValidationRule> entry : scriptMapping.entrySet()) {
            property = entry.getKey();
            rule = entry.getValue();
            subMonad = deriveFromParent(property, monad);
            source = provider.provideFor(property);
            rule.validate(source, subMonad);
        }
    }

    @FunctionalInterface
    public interface ValidationRule<T> {
        Maybe<T> validate(Object source, MaybeMonad workingMonad);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T arg);
    }

    private static class ValidationCommand<T> implements ValidationRule<T> {
        ValidationRule<T> rule;
        Callback<T> callback;

        @Override
        public Maybe<T> validate(Object source, MaybeMonad b) {
            return rule.validate(source, b).map(this::peek);
        }

        private T peek(T arg) {
            callback.call(arg);
            return arg;
        }
    }

}
