package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.step.ValidationProcesses;
import be.jdevelopment.tools.validation.step.SourcedValidationProcess;
import be.jdevelopment.tools.validation.step.ValidationProcessResult;
import be.jdevelopment.tools.validation.error.*;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class AddressFactory {
	
	private final SourcedValidationProcess validationProcess = ValidationProcesses.newSourcedValidationProcess()
		.<String> addStep(AddressProperty.STREET, AddressFactory::validateRequiredString)
		.<String> addStep(AddressProperty.POSTAL_CODE, AddressFactory::validatePostalCode);

    private final MonadOfProperties monad;
    AddressFactory(MonadOfProperties monad) {
        this.monad = monad;
    }

    private static class MutAddress {
        String street, postalCode;

        void setStreet(String arg) { street = arg; }
        void setPostalCode(String arg) { postalCode = arg; }
    }

    Address create(ObjectProvider provider) {
        MutAddress mutAddress = new MutAddress();

        /*
        ValidationProcesses.newAutoCommitProcess(monad, provider)
                .performStep(AddressProperty.STREET, AddressFactory::validateRequiredString, mutAddress::setStreet)
                .performStep(AddressProperty.POSTAL_CODE, AddressFactory::validatePostalCode, mutAddress::setPostalCode);
        */
        
        try(ValidationProcessResult result = validationProcess.performSteps(provider)) {
        	mutAddress.street = (String) result.get(AddressProperty.STREET.getName());
        	mutAddress.postalCode = (String) result.get(AddressProperty.POSTAL_CODE.getName());
        } catch(InvalidUserInputException invalidException) {
        	for(Failure failure: invalidException.getFailures())
        		monad.fail().registerFailure(failure);
        }

        return new Address(mutAddress.street, mutAddress.postalCode);
    }

    private static Property<String> validateRequiredString(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .filter(String.class::isInstance)
                .registerFailureCode("type")
                .map(String.class::cast);
    }

    private final static Pattern POSTAL_CODE_PATTERN = compile("^[0-9]+$");
    private static Property<String> validatePostalCode(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("required")
                .flatMap($ -> {
                    if ($ instanceof String strPostalCode) {
                        return monad.of(strPostalCode)
                                .filter(str -> POSTAL_CODE_PATTERN.matcher(str).matches())
                                .registerFailureCode("format");
                    } else if ($ instanceof Integer intPostalCode) {
                        return monad.of(intPostalCode)
                                .filter(i -> i > 0)
                                .registerFailureCode("format")
                                .map(String::valueOf);
                    }
                    return monad.<String>fail().registerFailureCode("type");
                });
    }

}
