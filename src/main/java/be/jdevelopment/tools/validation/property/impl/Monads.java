package be.jdevelopment.tools.validation.property.impl;

import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import static java.util.Objects.requireNonNull;

public final class Monads {

    private Monads() {}

    static public MonadOfProperties createOnFailureBuilder(FailureBuilder builder) {
        return new MonadStructure(requireNonNull(builder));
    }

}
