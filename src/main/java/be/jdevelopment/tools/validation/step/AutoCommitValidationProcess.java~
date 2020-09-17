package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import static java.util.Objects.requireNonNull;

public class AutoCommitValidationProcess extends ValidationProcessTemplate {

    private final MonadOfProperties monadOfProperties;
    private final ObjectProvider objectProvider;
    AutoCommitValidationProcess(MonadOfProperties monadOfProperties, ObjectProvider provider) {
        this.monadOfProperties = monadOfProperties;
        this.objectProvider = provider;
    }

    private void registerAction(ActionOnProvider actionOnProvider) {
        actionOnProvider.perform(monadOfProperties, objectProvider);
    }

    public <T> AutoCommitValidationProcess performStep(PropertyToken propertyToken, ValidationRule<? extends T> rule, Callback<? super T> andThen) {
        requireNonNull(propertyToken);
        requireNonNull(rule);
        requireNonNull(andThen);

        registerAction(new SimpleAction<T>(propertyToken, rule, andThen));
        return this;
    }

    public <T, U> AutoCommitValidationProcess performCollectionSteps(PropertyToken propertyToken,
                                                                     ValidationRule<? extends Iterable<T>> onCollectionRule,
                                                                     ValidationRule<U> onSingleRule,
                                                                     Callback<? super Iterable<U>> andThen) {
        requireNonNull(propertyToken);
        requireNonNull(onCollectionRule);
        requireNonNull(onSingleRule);
        requireNonNull(andThen);

        registerAction(new MultipleAction<>(propertyToken, onCollectionRule, onSingleRule, andThen));
        return this;
    }

}
