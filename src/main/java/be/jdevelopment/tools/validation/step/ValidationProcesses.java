package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

public class ValidationProcesses {

    public static AutoCommitValidationProcess newAutoCommitProcess(MonadOfProperties monadOfProperties, ObjectProvider provider) {
        return new AutoCommitValidationProcess(monadOfProperties, provider);
    }
    
    public static SourcedValidationProcess newSourcedValidationProcess() {
    	return new SourcedValidationProcess();
    }

}
