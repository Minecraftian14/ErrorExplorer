package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import java.util.Stack;

import static java.util.Objects.requireNonNull;

public class AutoCommitValidationProcess extends ValidationProcessTemplate {

    private final MonadOfProperties monadOfProperties;
    private final ObjectProvider objectProvider;
    AutoCommitValidationProcess(MonadOfProperties monadOfProperties, ObjectProvider provider) {
        this.monadOfProperties = requireNonNull(monadOfProperties);
        this.objectProvider = requireNonNull(provider);
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

    static class SimpleAction<T> implements ActionOnProvider {
        PropertyToken propertyToken;
        ValidationRule<? extends T> rule;
        Callback<? super T> andThen;
        SimpleAction(PropertyToken propertyToken, ValidationRule<? extends T> rule, Callback<? super T> andThen) {
            this.propertyToken = propertyToken;
            this.rule = rule;
            this.andThen = andThen;
        }

        @Override
        public void perform(MonadOfProperties monadOfProperties, ObjectProvider objectProvider) {
            performDestructuredAction(monadOfProperties, objectProvider, propertyToken, rule, andThen);
        }
    }

    static class MultipleAction<T, U> implements ActionOnProvider {
        PropertyToken propertyToken;
        ValidationRule<? extends Iterable<T>> onCollectionRule;
        ValidationRule<U> onSingleRule;
        Callback<? super Iterable<U>> andThen;
        MultipleAction(PropertyToken propertyToken,
                     ValidationRule<? extends Iterable<T>> onCollectionRule,
                     ValidationRule<U> onSingleRule,
                     Callback<? super Iterable<U>> andThen) {
            this.propertyToken = propertyToken;
            this.onCollectionRule = onCollectionRule;
            this.onSingleRule = onSingleRule;
            this.andThen = andThen;
        }

        @Override
        public void perform(MonadOfProperties monadOfProperties, ObjectProvider objectProvider) {
            var onEachItem = new ContextualizedCallback<T, U>();
            
            onEachItem.monad = monadOfProperties;
            onEachItem.propertyToken = propertyToken;
            onEachItem.validationRule = onSingleRule;
            onEachItem.andThen = andThen;
            
            performDestructuredAction(monadOfProperties, objectProvider, propertyToken, onCollectionRule, onEachItem::call);
        }
    }
    
    static class ContextualizedCallback<T,U> {

		MonadOfProperties monad;
		PropertyToken propertyToken;
		AutoCommitValidationProcess.ValidationRule<U> validationRule;
		AutoCommitValidationProcess.Callback<? super Iterable<U>> andThen;
	
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

}
