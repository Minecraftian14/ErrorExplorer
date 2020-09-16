package be.jdevelopment.tools.validation.completebasic;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.step.ValidationProcess;

import java.util.Objects;

public class InfoFactory {

    private final MonadOfProperties monad;

    InfoFactory(MonadOfProperties monad) {
        this.monad = monad;
    }

    private static class MutInfo {
        String data;

        void setData(String arg) {
            data = arg;
        }
    }

    Info create(ObjectProvider provider) {
        MutInfo mutInfo = new MutInfo();

        new ValidationProcess(provider, monad)
                .addStep(InfoProperty.DATA, InfoFactory::DataValidation, mutInfo::setData);

        return new Info(mutInfo.data);
    }

    private static Property<String> DataValidation(Object source, MonadOfProperties monad) {
        return monad.of(source)
                .filter(Objects::nonNull)
                .registerFailureCode("nullValue")
                .map(String.class::cast)
                .filter(arg -> arg.length() < 255)
                .registerFailureCode("longData");
    }

}
