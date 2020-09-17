package be.jdevelopment.tools.validation.property.impl;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.property.Property;
import be.jdevelopment.tools.validation.property.PropertyState;

class MutableProperty<T> extends AbstractProperty<T> {

    private Object value;
    MutableProperty(T value, MonadStructure structure) {
        super(STATE.SUCCESS, structure);
        this.value = value;
    }

    MutableProperty(MonadStructure structure) {
        super(STATE.FAIL_UNCUT, structure);
        value = null;
    }

    private Property<T> switchTo(STATE state) {
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
            Property<? extends U> nextProp = f.apply((T) value);
            return (Property<U>) (nextProp != null ? nextProp : switchTo(STATE.FAIL_UNCUT));
        }
        return (Property<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <U> Property<U> map(PropertyMap<? super T, ? extends U> f) {
        if (state == STATE.SUCCESS) {
            value = f.apply((T) value);
        }
        return (Property<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Property<T> filter(PropertyPredicate<? super T> predicate) {
        if (state == STATE.SUCCESS) {
            if (predicate.test((T) value)) {
                return this;
            }
            switchTo(STATE.FAIL_UNCUT);
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
            PropertyState exposedState;
            if (state == STATE.FAIL_UNCUT) exposedState = PropertyState.FAILURE;
            else if (state == STATE.SUCCESS) exposedState = PropertyState.SUCCESS;
            else throw new IllegalStateException(String.format("The provided state %s cannot be exposed", state.name()));

            Property<? extends U> nextProp = f.apply(exposedState, (T) value);
            return nextProp != null ? (Property<U>) nextProp : upperMonadStructureReference().fail();
        }
        return(Property<U>) this;
    }
}
