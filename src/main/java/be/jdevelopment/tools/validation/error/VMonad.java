package be.jdevelopment.tools.validation.error;

import be.jdevelopment.tools.validation.maybe.Maybe;
import be.jdevelopment.tools.validation.maybe.MaybeMonad;

public final class VMonad {

    private VMonad() {}

    static public <U> Maybe<U> of(U some, FailureBuilder builder) {
        Structure structure = new Structure(builder);
        return structure.of(some);
    }

    /* Implementations */

    static class Structure implements MaybeMonad {

        private FailureBuilder builder;
        Structure(FailureBuilder builder) {
            this.builder = builder;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Maybe<U> lift(Maybe<? extends U> maybe) {
            return (Maybe<U>) maybe;
        }

        @Override
        public <U> Maybe<U> of(U some) {
            return new Success<>(some, this);
        }

        private Failure<?> lazyFailure = null;
        @SuppressWarnings("unchecked")
        <U> Maybe<U> fail() {
            if (lazyFailure == null) {
                lazyFailure = new Failure<>(this);
            }
            return (Maybe<U>) lazyFailure;
        }
    }

    static abstract class _Maybe<T> implements Maybe<T> {

        final Structure structure;
        _Maybe(Structure structure) {
            this.structure = structure;
        }

        @Override
        final public MaybeMonad upperMonadStructureReference() {
            return structure;
        }
    }

    static class Success<T> extends _Maybe<T> {
        private final T value;

        Success(T value, Structure structure) {
            super(structure);
            this.value = value;
        }

        @Override public final
        <U> Maybe<U> flatMap(MaybeMap<? super T, ? extends Maybe<? extends U>> f) {
            return upperMonadStructureReference().lift(f.apply(value));
        }

        @Override public final
        <U> Maybe<U> map(MaybeMap<? super T, ? extends U> f) {
            return upperMonadStructureReference().of(f.apply(value));
        }

        @Override public final
        Maybe<T> filter(MaybePredicate<? super T> predicate) {
            return predicate.test(value) ? this : structure.fail();
        }

        @Override public final
        Maybe<T> tap(MaybeTap tap) {
            return this;
        }

        @Override public final
        Maybe<T> registerFailureCode(String code) {
            return this;
        }
    }

    static class Failure<T> extends _Maybe<T> {

        private boolean isCut = false;
        Failure(Structure structure) {
            super(structure);
        }

        @Override public final
        <U> Maybe<U> flatMap(MaybeMap<? super T, ? extends Maybe<? extends U>> f) {
            return structure.fail();
        }

        @Override public final
        <U> Maybe<U> map(MaybeMap<? super T, ? extends U> f) {
            return structure.fail();
        }

        @Override public final
        Maybe<T> filter(MaybePredicate<? super T> predicate) {
            return structure.fail();
        }

        @Override public final
        Maybe<T> tap(MaybeTap tap) {
            if (!isCut) {
                tap.tap();
                isCut = true;
            }
            return structure.fail();
        }

        @Override public final
        Maybe<T> registerFailureCode(String code) {
            if(!isCut) {
                structure.builder.withCode(code);
                isCut = true;
            }
            return structure.fail();
        }
    }

}
