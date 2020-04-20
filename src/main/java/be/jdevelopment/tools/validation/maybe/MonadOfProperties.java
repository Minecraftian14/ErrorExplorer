package be.jdevelopment.tools.validation.maybe;

public interface MonadOfProperties {

    <U> Property<U> lift(Property<? extends U> property);

    <U> Property<U> of(U some);

    <U> Property<U> fail();

}
