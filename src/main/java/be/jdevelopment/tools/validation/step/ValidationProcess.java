package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.error.MonadFactory;
import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.*;
import java.util.stream.IntStream;

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

    public <T> ValidationProcess addStep(PropertyToken propertyToken, ValidationRule<? extends T> rule, Callback<? super T> andThen) {
        MonadOfProperties subMonad = deriveFromParent(propertyToken, monad);
        Object source = provider.provideFor(propertyToken);
        rule.validate(source, subMonad).map($ -> { andThen.call($); return null; });

        return this;
    }

    public <T, U> ValidationProcess addCollectionSteps(PropertyToken propertyToken, ValidationRule<? extends Iterator<T>> onCollectionRule,
                                                       ValidationRule<U> onSingleRule, Callback<? super Iterator<U>> andThen) {
        Callback<Iterator<T>> onValidCollectionCallback = collection -> {
            List<U> collected = new ArrayList<>();

            int i = 0;
            SimpleBox<U> box = new SimpleBox<>();
            while (collection.hasNext()) {
                PropertyToken property = new DynamicCollectionProperty(i, propertyToken);
                Object resource = collection.next();
                box.set(null);
                new ValidationProcess($ -> resource, monad).addStep(property, onSingleRule, box::set).execute();
                if (box.value != null) collected.add(box.value);
                i++;
            }

            andThen.call(collected.iterator());
        };

        return this.addStep(propertyToken, onCollectionRule, onValidCollectionCallback);
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
        ValidationRule<? extends T> rule;
        Callback<? super T> callback;

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
