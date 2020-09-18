package be.jdevelopment.tools.validation.property.impl;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.property.PropertyState;

class MutableProperty<T> extends AbstractProperty<T> {

    Object value;
    MutableProperty(T value, MonadStructure structure) {
        super(STATE.SUCCESS, structure);
        this.value = value;
    }

    MutableProperty(MonadStructure structure) {
        super(STATE.FAIL_UNCUT, structure);
        value = null;
    }

    MutableProperty<T> switchTo(STATE state) {
        this.state = state;
        if (state != STATE.SUCCESS) {
            value = null; // free reference
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <U> Property<U> flatMap(PropertyMap<? super T, ? extends Property<? extends U>> f) {
        if (state == STATE.SUCCESS) {
            Property<? extends U> nextProp = null;
            try {
            	nextProp = f.apply((T) value);
            } finally {
            	return (Property<U>) (nextProp != null ? nextProp : switchTo(STATE.FAIL_UNCUT));
            }
        }
        return (Property<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <U> Property<U> map(PropertyMap<? super T, ? extends U> f) {
        if (state == STATE.SUCCESS) {
        	boolean success = false;
        	try {
        		value = f.apply((T) value);
        		success = true;
        	} finally {
        		if (!success) switchTo(STATE.FAIL_UNCUT);
        	}
        }
        return (Property<U>) this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public final Property<T> or (PropertySupplier<? extends T> supplier) {
    	if (state == STATE.FAIL_UNCUT) {
    		Property<? extends T> alternate = null;
    		try {
    			alternate = supplier.supply();
    		} finally {
    			return alternate == null ? this : (Property<T>) alternate;
    		}
    	}
    	return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Property<T> filter(PropertyPredicate<? super T> predicate) {
        if (state == STATE.SUCCESS) {
        	boolean predicateOk = false;
        	try {
        		predicateOk = predicate.test((T) value);
        	} finally {
        		if (!predicateOk) switchTo(STATE.FAIL_UNCUT);
        	}
        }
        return this;
    }

    @Override public final Property<T> registerFailureCode(String code) {
        if(state == STATE.FAIL_UNCUT) {
            upperBuilderReference().withCode(code);
            switchTo(STATE.FAIL_CUT);
        }
        return this;
    }

    @Override public final Property<T> registerFailure(Failure failure) {
        if(state == STATE.FAIL_UNCUT) {
            upperBuilderReference().withFailure(failure); // Directly use withFailure of the builder
            switchTo(STATE.FAIL_CUT);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <U> Property<U> match(PropertyMatch<? super T, ? extends Property<? extends U>> f) {
        if (state != STATE.FAIL_CUT) {
            PropertyState exposedState = null;
            if (state == STATE.FAIL_UNCUT) exposedState = PropertyState.FAILURE;
            else if (state == STATE.SUCCESS) exposedState = PropertyState.SUCCESS;
            assert exposedState != null : "Exposed state is not expected to be null";

            Property<? extends U> nextProp = null;
            try {
            	nextProp = f.apply(exposedState, (T) value);
            } finally {
            	return nextProp != null ? (Property<U>) nextProp :
            		exposedState == PropertyState.FAILURE ? (Property<U>) this : upperMonadStructureReference().fail();
            }
        }
        return(Property<U>) this;
    }
}
