package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.error.MonadFactory;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import java.util.*;

public class ValidationProcess {

    private final ObjectProvider provider;
    protected MonadOfProperties monad;

    public ValidationProcess(ObjectProvider provider, MonadOfProperties monad) {
        this.monad = monad;
        this.provider = provider;
    }

    private static MonadOfProperties deriveFromParent(PropertyToken propertyToken, MonadOfProperties baseMonad) {
        return MonadFactory.on(errorCode -> baseMonad.fail().registerFailureCode(String.format("%s.%s", propertyToken.getName(), errorCode)));
    }

    @FunctionalInterface
    public interface ValidationRule<T> {
        Property<T> validate(Object source, MonadOfProperties workingMonad);
    }

    public <T> ValidationProcess addStep(PropertyToken propertyToken, ValidationRule<? extends T> rule, Callback<? super T> andThen) {
        MonadOfProperties subMonad = deriveFromParent(propertyToken, monad);
        Object source = provider.provideFor(propertyToken);
        rule.validate(source, subMonad).map($ -> { andThen.call($); return null; });

        return this;
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T arg);
    }

    public <T, U> ValidationProcess addCollectionSteps(PropertyToken propertyToken,
                                                       ValidationRule<? extends Iterator<T>> onCollectionRule,
                                                       ValidationRule<U> onSingleRule,
                                                       Callback<? super Iterator<U>> andThen) {
        Callback<Iterator<T>> onEachItem = new ContextualizedCallback<T, U>
                (monad, propertyToken, onSingleRule, andThen)::call;

        return this.addStep(propertyToken, onCollectionRule, onEachItem);
    }

}
