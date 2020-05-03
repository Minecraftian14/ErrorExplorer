package be.jdevelopment.tools.validation.simple;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.InvalidUserInputException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonValidationTest {

    @Test
    public void should_validateProvider_givenBasicProperties() throws InvalidUserInputException {

        ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.provideFor(PersonProperty.EMAIL)).thenReturn("hello.world@universe.com");

        Person pojo = PersonFactory.create(provider);

        assertEquals("hello.world@universe.com", pojo.emailAddress());
    }

    @Test
    public void should_invalidateProvider_givenInvalidEmail() {

        ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.provideFor(PersonProperty.EMAIL)).thenReturn("hello.world_at_universe.com");

        try {
            PersonFactory.create(provider);
            fail("Should have thrown validation error");
        } catch(InvalidUserInputException e) {
            assertEquals(1, e.getFailures().size());
            assertTrue(e.getFailures().stream().map(Failure::getCode).anyMatch("emailAddress.format"::equals));
        }

    }

    @Test
    public void should_invalidateProvider_givenNoEmail() {

        ObjectProvider provider = mock(ObjectProvider.class);

        try {
            PersonFactory.create(provider);
            fail("Should have thrown validation error");
        } catch(InvalidUserInputException e) {
            assertEquals(1, e.getFailures().size());
            assertTrue(e.getFailures().stream().map(Failure::getCode).anyMatch("emailAddress.required"::equals));
        }
    }

    @Test
    public void should_invalidateProvider_givenNumericInput() {

        ObjectProvider provider = mock(ObjectProvider.class);
        when(provider.provideFor(PersonProperty.EMAIL)).thenReturn(0);

        try {
            PersonFactory.create(provider);
            fail("Should have thrown validation error");
        } catch(InvalidUserInputException e) {
            assertEquals(1, e.getFailures().size());
            assertTrue(e.getFailures().stream().map(Failure::getCode).anyMatch("emailAddress.type"::equals));
        }

    }

}
