package be.jdevelopment.tools.validation.error;

import be.jdevelopment.tools.validation.maybe.Property;
import be.jdevelopment.tools.validation.maybe.MonadOfProperties;

public final class MonadFactory {

    private MonadFactory() {}

    static public MonadOfProperties on(FailureBuilder builder) {
        return new Structure(builder);
    }

    /* Implementations */

    static class Structure implements MonadOfProperties {

        private FailureBuilder builder;
        Structure(FailureBuilder builder) {
            this.builder = builder;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Property<U> lift(Property<? extends U> property) {
            return (Property<U>) property;
        }

        @Override
        public <U> Property<U> of(U some) {
            return new Success<>(some, this);
        }

        @SuppressWarnings("unchecked")
        @Override public final
        <U> Property<U> fail() {
            return (Property<U>) new Failure<>(this);
        }
    }

    static abstract class _Property<T> implements Property<T> {

        final Structure structure;
        _Property(Structure structure) {
            this.structure = structure;
        }

        @Override
        final public MonadOfProperties upperMonadStructureReference() {
            return structure;
        }
    }

    static class Success<T> extends _Property<T> {
        private final T value;

        Success(T value, Structure structure) {
            super(structure);
            this.value = value;
        }

        @Override public final
        <U> Property<U> flatMap(MaybeMap<? super T, ? extends Property<? extends U>> f) {
            return upperMonadStructureReference().lift(f.apply(value));
        }

        @Override public final
        <U> Property<U> map(MaybeMap<? super T, ? extends U> f) {
            return upperMonadStructureReference().of(f.apply(value));
        }

        @Override public final Property<T> filter(MaybePredicate<? super T> predicate) {
            return predicate.test(value) ? this : structure.fail();
        }

        @Override public final Property<T> registerFailureCode(String code) {
            return this;
        }

        @Override public final boolean isFailure() { return false; }
    }

    static class Failure<T> extends _Property<T> {

        private boolean isCut = false;
        Failure(Structure structure) {
            super(structure);
        }

        @SuppressWarnings("unchecked")
        @Override public final
        <U> Property<U> flatMap(MaybeMap<? super T, ? extends Property<? extends U>> f) {
            return (Property<U>) this;
        }

        @SuppressWarnings("unchecked")
        @Override public final
        <U> Property<U> map(MaybeMap<? super T, ? extends U> f) {
            return (Property<U>) this;
        }

        @Override public final Property<T> filter(MaybePredicate<? super T> predicate) {
            return this;
        }

        @Override public final Property<T> registerFailureCode(String code) {
            if(!isCut) {
                structure.builder.withCode(code);
                isCut = true;
            }
            return this;
        }

        @Override public final boolean isFailure() { return true; }
    }

}
