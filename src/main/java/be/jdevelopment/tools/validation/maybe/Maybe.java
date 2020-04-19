package be.jdevelopment.tools.validation.maybe;

import be.jdevelopment.tools.validation.error.FailureBuilder;

public interface Maybe<T> {

    /* Upper monad structure reference */

    MaybeMonad upperMonadStructureReference();

    /* Monad interface */

    @FunctionalInterface
    interface MaybeMap<U, V> {
        V apply(U arg);
    }

    <U> Maybe<U> flatMap(MaybeMap<? super T, ? extends Maybe<? extends U>> f);

    <U> Maybe<U> map(MaybeMap<? super T, ? extends U> f);

    @FunctionalInterface
    interface MaybePredicate<U> {
        boolean test(U arg);
    }

    Maybe<T> filter(MaybePredicate<? super T> predicate);

    @FunctionalInterface
    interface MaybeTap {
        void tap();
    }

    Maybe<T> tap(MaybeTap tap);

    Maybe<T> registerFailureCode(String code);

}
