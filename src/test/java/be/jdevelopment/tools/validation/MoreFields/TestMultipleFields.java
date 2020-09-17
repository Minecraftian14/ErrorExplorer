package be.jdevelopment.tools.validation.MoreFields;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.impl.Monads;
import be.jdevelopment.tools.validation.util.ObjectProviderHelper;
import org.junit.Before;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMultipleFields {

    private HashSet<Failure> failures;
    private FailureBuilder failureBuilder;

    @Before
    public void setUp() {
        failures = new HashSet<>();
        failureBuilder = errorCode -> failures.add(errorCode::toString);
    }

    @org.junit.Test
    public void testSuccess() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("morefields/contactS.json");

        Contact contact = new ContactFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        failures.forEach(failure -> System.out.println(failure.getCode()));

        assertTrue(failures.isEmpty());
        assertEquals(17, contact.name().length());
        assertEquals(56896, contact.number());
        assertEquals(7800, contact.debt(), 20);
    }

    @org.junit.Test
    public void testFailureName() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("morefields/contactF_name.json");

        Contact contact = new ContactFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(1, failures.size());
        assertEquals(1, failures.stream().filter(failure -> failure.getCode().equals("name.formatError")).count());
    }

    @org.junit.Test
    public void testFailureNumber() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("morefields/contactF_number.json");

        Contact contact = new ContactFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(1, failures.size());
        assertEquals(1, failures.stream().filter(failure -> failure.getCode().equals("number.invalidLength")).count());
    }

    @org.junit.Test
    public void testFailureDebt() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("morefields/contactF_debt.json");

        Contact contact = new ContactFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(1, failures.size());
        assertEquals(1, failures.stream().filter(failure -> failure.getCode().equals("debt.notPositive")).count());
    }

}
