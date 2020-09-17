package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import java.util.Stack;

class ContextualizedCallback<T,U> {

    private final MonadOfProperties monad;
    private final PropertyToken propertyToken;
    private final AutoCommitValidationProcess.ValidationRule<U> validationRule;
    private final AutoCommitValidationProcess.Callback<? super Iterable<U>> andThen;
    ContextualizedCallback(
            MonadOfProperties monad,
            PropertyToken propertyToken,
            AutoCommitValidationProcess.ValidationRule<U> onSingleRule,
            AutoCommitValidationProcess.Callback<? super Iterable<U>> andThen) {
        this.monad = monad;
        this.propertyToken = propertyToken;
        this.validationRule = onSingleRule;
        this.andThen = andThen;
    }

    void call(Iterable<T> collection) {
        Stack<U> collected = new Stack<>();
        SimpleBox<U> box = new SimpleBox<>();

        int i = 0;
        for (Object resource : collection) {
            box.reset();
                new AutoCommitValidationProcess(monad, $ -> resource)
                    .performStep(new DynamicCollectionProperty(i, propertyToken), validationRule, box::set);
            if (box.isNonEmpty) collected.add(box.value);
            i++;
        }

        andThen.call(collected);
    }

}
