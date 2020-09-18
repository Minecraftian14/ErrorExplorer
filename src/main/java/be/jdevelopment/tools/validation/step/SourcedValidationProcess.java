package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.impl.Monads;
import be.jdevelopment.tools.validation.error.InvalidUserInputException;
import be.jdevelopment.tools.validation.error.Failure;

import java.util.Stack;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class SourcedValidationProcess extends ValidationProcessTemplate {

    SourcedValidationProcess() {}
    
    @FunctionalInterface
    interface LaterAction {
    	void perform(AutoCommitValidationProcess process, Callback<Object> successHandler);
    }
    
    private Stack<LaterAction> actions = new Stack<>();
    private Stack<PropertyToken> properties = new Stack<>();

    public <T> SourcedValidationProcess addStep(PropertyToken propertyToken, ValidationRule<? extends T> rule) {
    	actions.add((autoCommitDelegationProcess, successHandler) ->
    		autoCommitDelegationProcess.performStep(propertyToken, rule, successHandler)
    	);
    	properties.add(propertyToken);
    	
    	assert properties.size() == actions.size() : "For some reason, properties and actions are corrupted in size";
        return this;
    }

    public <T, U> SourcedValidationProcess addCollectionSteps(PropertyToken propertyToken,
                                                                     ValidationRule<? extends Iterable<T>> onCollectionRule,
                                                                     ValidationRule<U> onSingleRule) {
        actions.add((autoCommitDelegationProcess, successHandler)
        	-> autoCommitDelegationProcess.performCollectionSteps(propertyToken, onCollectionRule, onSingleRule, successHandler)
        );
        properties.add(propertyToken);
        
        assert properties.size() == actions.size() : "For some reason, properties and actions are corrupted in size";
        return this;
    }
    
    public ValidationProcessResult performSteps(ObjectProvider provider) throws InvalidUserInputException {
    	var errorStack = new Stack<String>();
    	var monad = Monads.createOnFailureBuilder(errorStack::add);
    	var autoCommitDelegationProcess = ValidationProcesses.newAutoCommitProcess(monad, provider);
    	
    	assert properties.size() == actions.size() : "For some reason, properties and actions are corrupted in size";
    	
    	var results = new Stack<Object>();
    	var simpleBox = new SimpleBox();
    	for(LaterAction action : actions) {
    		assert !simpleBox.isNonEmpty : "Simple box should be clean at each step of the look";
    		action.perform(autoCommitDelegationProcess, simpleBox::set);
    		if (simpleBox.isNonEmpty) {
    			results.add(simpleBox.value);
    		}
    		simpleBox.reset();
    	}
    	
    	if (!errorStack.isEmpty()) {
    		throw new InvalidUserInputException(
    			errorStack.stream().<Failure> map(code -> code::toString).collect(toList())
    			);
    	}
    	
    	var fixedProperties = properties.stream().map(PropertyToken::getName).toArray(String[]::new);
    	var fixedResults = results.stream().toArray();
    	
    	if (fixedProperties.length != fixedResults.length)
    		throw new InvalidUserInputException(String.format("Unreported error occured",
    			fixedProperties.length,
    			fixedResults.length)
    		);
    	
    	return new ValidationProcessResult(fixedProperties, fixedResults);
    }
}
