package be.jdevelopment.tools.validation.property.impl;

import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;

class MonadStructure implements MonadOfProperties {

    final FailureBuilder builder;
    MonadStructure(FailureBuilder builder) {
        this.builder = builder;
    }

    @Override
    public <U> Property<U> of(U some) {
        return new MutableProperty<>(some, this);
    }

    @SuppressWarnings("unchecked")
    @Override public final <U> Property<U> fail() {
        return (Property<U>) new MutableProperty<>(this);
    }
}
