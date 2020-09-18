package be.jdevelopment.tools.validation.property;

import be.jdevelopment.tools.validation.error.Failure;

public interface Property<T> {

    /* Monad interface */

    @FunctionalInterface
    interface PropertyMap<U, V> {
        V apply(U arg);
    }

    <U> Property<U> flatMap(PropertyMap<? super T, ? extends Property<? extends U>> f);

    <U> Property<U> map(PropertyMap<? super T, ? extends U> f);

    Property<T> registerFailureCode(String code);

    Property<T> registerFailure(Failure code);

    /* Failure or Success discrimination */
    
    @FunctionalInterface
    interface PropertySupplier<U> {
    	Property<U> supply();
    }
    
    Property<T> or(PropertySupplier<? extends T> supplier);

    @FunctionalInterface
    interface PropertyPredicate<U> {
        boolean test(U arg);
    }

    Property<T> filter(PropertyPredicate<? super T> predicate);

    @FunctionalInterface
    interface PropertyMatch<U, V> {
        V apply(PropertyState sate, U arg);
    }
    <U> Property<U> match(PropertyMatch<? super T, ? extends Property<? extends U>> f);

}
