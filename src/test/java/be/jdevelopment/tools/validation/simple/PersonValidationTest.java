package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.annotations.UnsafeProvider;
import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonValidationTest {

    /* Validation process */

    private HashSet<Failure> failures;
    private FailureBuilder failureBuilder;

    @Before
    public void setUp() {
        failures = new HashSet<>();
        failureBuilder = errorCode -> failures.add(() -> errorCode);
    }

    @Test
    public void should_validateProvider_givenBasicProperties() {

        @UnsafeProvider ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.provideForm(Person.EMAIL_PROPERTY)).thenReturn("hello.world@universe.com");

        Person pojo = new PersonBuilder(provider, failureBuilder).build();

        assertTrue(failures.isEmpty());
        assertEquals("hello.world@universe.com", pojo.emailAddress);
    }

    @Test
    public void should_invalidateProvider_givenInvalidEmail() {

        @UnsafeProvider ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.provideForm(Person.EMAIL_PROPERTY)).thenReturn("hello.world_at_universe.com");

        new PersonBuilder(provider, failureBuilder).build();

        assertEquals(1, failures.size());
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddress.format"::equals));
    }

    @Test
    public void should_invalidateProvider_givenNoEmail() {

        @UnsafeProvider ObjectProvider provider = mock(ObjectProvider.class);

        new PersonBuilder(provider, failureBuilder).build();

        assertEquals(1, failures.size());
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddress.required"::equals));
    }

    @Test
    public void should_invalidateProvider_givenNumericInput() {

        @UnsafeProvider ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.provideForm(Person.EMAIL_PROPERTY)).thenReturn(0);

        new PersonBuilder(provider, failureBuilder).build();

        assertEquals(1, failures.size());
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddress.type"::equals));
    }

}
