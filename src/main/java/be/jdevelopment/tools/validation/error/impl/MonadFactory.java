package be.jdevelopment.tools.validation.error.impl;

import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import static java.util.Objects.requireNonNull;

public final class MonadFactory {

    private MonadFactory() {}

    static public MonadOfProperties on(FailureBuilder builder) {
        return new MonadStructure(requireNonNull(builder));
    }

}
