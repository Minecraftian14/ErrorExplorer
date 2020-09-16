package be.jdevelopment.tools.validation.MoreFields;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.Objects;
import java.util.regex.Pattern;

public class ContactFactory {

    private final MonadOfProperties monad;

    ContactFactory(MonadOfProperties monad) {
        this.monad = monad;
    }

    private static class MutContact {
        String name;
        int number;
        double debt;

        public void setName(String name) {
            this.name = name;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public void setDebt(double debt) {
            this.debt = debt;
        }
    }

    Contact create(ObjectProvider provider) {
        MutContact mutContact = new MutContact();

        ValidationProcess process = new ValidationProcess(provider, monad)
                .addStep(ContactProperties.NAME, ContactFactory::NameValidation, mutContact::setName)
                .addStep(ContactProperties.NUMBER, ContactFactory::NumberValidation, mutContact::setNumber)
                .addStep(ContactProperties.DEBT,
                        ContactFactory::DebtValidation,
                        mutContact::setDebt);

        return new Contact(mutContact.name, mutContact.number, mutContact.debt);
    }

    static Pattern patName = Pattern.compile("[0-9]{3}[- ][\\w ]*");

    private static Property<String> NameValidation(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("nullValue")
                .map(String.class::cast)
                .filter(arg -> patName.matcher(arg).matches())
                .registerFailureCode("formatError");
    }

    private static Property<Integer> NumberValidation(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("nullValue")
                .map(Integer.class::cast)
                .filter(arg -> arg < 99999 && arg > 10000)
                .registerFailureCode("invalidLength");
    }

    private static Property<Double> DebtValidation(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("nullValue")
                .map(Double.class::cast)
                .filter(arg -> arg > 0)
                .registerFailureCode("notPositive");
    }

}
