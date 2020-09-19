package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.property.impl.Monads;

import static java.util.Objects.requireNonNull;

abstract class ValidationProcessTemplate {

    protected static MonadOfProperties deriveFromParent(PropertyToken propertyToken, MonadOfProperties baseMonad) {
        assert propertyToken != null :
                "Property Token is null. This should have been handled by parent caller";
        assert baseMonad != null :
                "Base monad to derive is null. This should have been handled by parent caller";

        return Monads.createOnFailureBuilder(new ChildMonadFailureBuilder(baseMonad, propertyToken));
    }

    static class ChildMonadFailureBuilder implements FailureBuilder {
        private final MonadOfProperties baseMonad;
        private final PropertyToken property;

        ChildMonadFailureBuilder(MonadOfProperties baseMonad, PropertyToken property) {
            assert property != null :
                    "Property Token is null. This should have been handled by parent caller";
            assert baseMonad != null :
                    "Base monad to derive is null. This should have been handled by parent caller";
            this.property = property;
            this.baseMonad = baseMonad;
        }

        @Override
        public void withCode(String errorCode, String message) {
            if (message != null && !message.strip().equals(""))
                baseMonad.fail().registerFailureCode(String.format("%s.%s: %s ", property.getName(), errorCode, message));
            else
                baseMonad.fail().registerFailureCode(String.format("%s.%s", property.getName(), errorCode));
        }
    }

    @FunctionalInterface
    public interface ValidationRule<T> {
        Property<? extends T> validate(Object source, MonadOfProperties workingMonad);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T arg);

        default Void asVoidOperator(T arg) {
            call(arg);
            return null;
        }
    }

    @FunctionalInterface
    interface ActionOnProvider {
        void perform(MonadOfProperties monadOfProperties, ObjectProvider provider);
    }

    static <T> void performDestructuredAction(MonadOfProperties monadOfProperties, ObjectProvider objectProvider,
                                              PropertyToken propertyToken, ValidationRule<? extends T> rule, Callback<? super T> andThen) {
        requireNonNull(propertyToken);
        requireNonNull(monadOfProperties);
        MonadOfProperties subMonad = deriveFromParent(propertyToken, monadOfProperties);
        Object source = objectProvider.provideFor(propertyToken);
        requireNonNull(rule.validate(source, subMonad),
                "Unable to continue validation step when result of validator is null reference")
                .map(andThen::asVoidOperator);
    }

}
