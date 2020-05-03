package be.jdevelopment.tools.validation.error;

import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.PropertyState;

import java.util.Objects;

public final class MonadFactory {

    private MonadFactory() {}

    static public MonadOfProperties on(FailureBuilder builder) {
        return new Structure(builder);
    }

    /* Implementations */

    static class Structure implements MonadOfProperties {

        private FailureBuilder builder;
        Structure(FailureBuilder builder) {
            this.builder = Objects.requireNonNull(builder);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Property<U> lift(Property<? extends U> property) {
            return property != null ? (Property<U>) property : fail();
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
            Property<? extends U> nextProp = f.apply(value);
            return nextProp != null
                    ? upperMonadStructureReference().lift(nextProp)
                    : upperMonadStructureReference().fail();
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

        @Override public <U> Property<U> match(MaybeMatch<? super T, ? extends Property<? extends U>> f) {
            Property<? extends U> nextProp = f.apply(PropertyState.SUCCESS, value);
            return nextProp != null
                    ? upperMonadStructureReference().lift(nextProp)
                    : upperMonadStructureReference().fail();
        }
    }

    static class Failure<T> extends _Property<T> {

        private boolean isCut;
        Failure(Structure structure) {
            this(structure, false);
        }
        Failure(Structure structure, boolean isCut) {
            super(structure);
            this.isCut = isCut;
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

        @SuppressWarnings("unchecked")
        @Override public <U> Property<U> match(MaybeMatch<? super T, ? extends Property<? extends U>> f) {
            Property<? extends U> matchResult = f.apply(PropertyState.FAILURE, null);
            return matchResult != null
                    ? upperMonadStructureReference().lift(f.apply(PropertyState.FAILURE, null))
                    : (Property<U>) this;
        }
    }

}
