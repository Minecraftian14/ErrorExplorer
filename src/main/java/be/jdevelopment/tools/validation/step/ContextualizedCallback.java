package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

class ContextualizedCallback<T,U> {

    private final MonadOfProperties monad;
    private final PropertyToken propertyToken;
    private final ValidationProcess.ValidationRule<U> validationRule;
    private final ValidationProcess.Callback<? super Iterator<U>> andThen;
    ContextualizedCallback(
            MonadOfProperties monad,
            PropertyToken propertyToken,
            ValidationProcess.ValidationRule<U> onSingleRule,
            ValidationProcess.Callback<? super Iterator<U>> andThen) {
        this.monad = monad;
        this.propertyToken = propertyToken;
        this.validationRule = onSingleRule;
        this.andThen = andThen;
    }

    void call(Iterator<T> collection) {
        List<U> collected = new Stack<>();

        int i = 0;
        SimpleBox<U> box = new SimpleBox<>();
        while (collection.hasNext()) {
            PropertyToken property = new DynamicCollectionProperty(i, propertyToken);
            Object resource = collection.next();
            box.set(null);
            new ValidationProcess($ -> resource, monad).addStep(property, validationRule, box::set);
            if (box.value != null) collected.add(box.value);
            i++;
        }

        andThen.call(collected.iterator());
    }

}
