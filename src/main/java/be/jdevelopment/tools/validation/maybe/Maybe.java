package be.jdevelopment.tools.validation.maybe;

public interface Maybe<T> {

    /* Upper monad structure reference */

    MaybeMonad upperMonadStructureReference();

    /* Object wrapper interface */

    boolean isSuccess();

    T get();

    /* Monad interface */

    default <U> Maybe<U> flatMap(MaybeMap<? super T, ? extends Maybe<? extends U>> f) {
        if (isSuccess()) {
            return upperMonadStructureReference().lift(f.apply(get()));
        }
        return upperMonadStructureReference().fail();
    }

    default <U> Maybe<U> map(MaybeMap<? super T, ? extends U> f) {
        if (isSuccess()) {
            return flatMap(t -> upperMonadStructureReference().of(f.apply(t)));
        }
        return upperMonadStructureReference().fail();
    }

    default Maybe<T> filter(MaybePredicate<? super T> predicate) {
        if (isSuccess() && predicate.test(get())) {
            return this;
        }
        return upperMonadStructureReference().fail();
    }

    /* Filter procedure */

    default Maybe<T> tap(MaybeTap tap) {
        if (!isSuccess()) {
            tap.tap();
        }
        return this;
    }

    Maybe<T> cut(MaybeTap tap); // Same as tap, but the remaining part of the chain is ignored

    /* tap on failure */

    default Maybe<T> peek(MaybePeek<T> peek) {
        if (isSuccess()) {
            peek.peek(get());
        }
        return this;
    }

    @FunctionalInterface
    interface MaybeMap<U, V> {
        V apply(U arg);
    }

    @FunctionalInterface
    interface MaybePredicate<U> {
        boolean test(U arg);
    }

    @FunctionalInterface
    interface MaybeTap {
        void tap();
    }

    @FunctionalInterface
    interface MaybePeek<U> {
        void peek(U arg);
    }

}
