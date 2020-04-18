package be.jdevelopment.tools.validation.maybe;

public final class VMonad {
    private final static Structure MONAD = new Structure();

    private VMonad() {}

    static public <U> Maybe<U> of(U some) {
        return MONAD.of(some);
    }

    /* Implementations */

    static class Structure implements MaybeMonad {
        private final static Failure<?> FAILURE = new Failure<>();

        @Override
        @SuppressWarnings("unchecked")
        public <U> Maybe<U> lift(Maybe<? extends U> maybe) {
            return (Maybe<U>) maybe;
        }

        @Override
        public <U> Maybe<U> of(U some) {
            return new Success<>(some);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Maybe<U> fail() {
            return (Maybe<U>) FAILURE;
        }
    }

    static abstract class _Maybe<T> implements Maybe<T> {
        @Override
        final public MaybeMonad upperMonadStructureReference() {
            return MONAD;
        }
    }

    static class Success<T> extends _Maybe<T> {
        private final T value;

        Success(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public final Maybe<T> cut(MaybeTap tap) {
            return this;
        }
    }

    static class Failure<T> extends _Maybe<T> {
        @Override
        public T get() {
            return null;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Maybe<T> cut(MaybeTap tap) {
            tap.tap();
            return (Maybe<T>) Cutted.CUTTED;
        }
    }

    static class Cutted<T> extends Failure<T> {
        private final static Cutted<?> CUTTED = new Cutted<>();

        @Override
        @SuppressWarnings("unchecked")
        public final Maybe<T> tap(MaybeTap tap) {
            return (Maybe<T>) CUTTED;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Maybe<T> cut(MaybeTap tap) {
            return (Maybe<T>) Cutted.CUTTED;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Maybe<U> flatMap(MaybeMap<? super T, ? extends Maybe<? extends U>> f) {
            return (Maybe<U>) CUTTED;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Maybe<U> map(MaybeMap<? super T, ? extends U> f) {
            return (Maybe<U>) CUTTED;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Maybe<T> filter(MaybePredicate<? super T> p) {
            return (Maybe<T>) CUTTED;
        }
    }

}
