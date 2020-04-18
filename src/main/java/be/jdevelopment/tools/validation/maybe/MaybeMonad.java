package be.jdevelopment.tools.validation.maybe;

public interface MaybeMonad {

    <U> Maybe<U> lift(Maybe<? extends U> maybe);

    <U> Maybe<U> of(U some);

    <U> Maybe<U> fail();

}
