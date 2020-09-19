package be.jdevelopment.tools.validation.completebasic;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.impl.Monads;
import be.jdevelopment.tools.validation.util.ObjectProviderHelper;
import org.junit.Before;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCompleteBasics {

    // failures.forEach(failure -> System.out.println(failure.getCode()));

    private HashSet<Failure> failures;
    private FailureBuilder failureBuilder;

    @Before
    public void setUp() {
        failures = new HashSet<>();
        failureBuilder = FailureBuilder.getDefault(failures);
    }

    @org.junit.Test
    public void testSuccess() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("completebasic/infoS.json");

        Info info = new InfoFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertTrue(failures.isEmpty());
        assertEquals(69, info.data().length());
    }

    @org.junit.Test
    public void testFailureNull() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("completebasic/infoF1.json");

        new InfoFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(1, failures.size());
        assertEquals(1, failures.stream().filter(failure -> failure.getCode().equals("data.nullValue")).count());
    }

    @org.junit.Test
    public void testFailureTooLong() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("completebasic/infoF2.json");

        new InfoFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(1, failures.size());
        assertEquals(1, failures.stream().filter(failure -> failure.getCode().startsWith("data.longData")).count());
    }

}
