package be.jdevelopment.tools.validation.completebasic;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.error.impl.MonadFactory;
import be.jdevelopment.tools.validation.util.ObjectProviderProvider;
import org.junit.Before;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCompleteBasics {

    private HashSet<Failure> failures;
    private FailureBuilder failureBuilder;

    @Before
    public void setUp() {
        failures = new HashSet<>();
        failureBuilder = errorCode -> failures.add(() -> errorCode);
    }

    @org.junit.Test
    public void testSuccess() throws Exception {
        var provider = ObjectProviderProvider.fromJsonFile("completebasic/infoS.json");

        Info info = new InfoFactory(MonadFactory.on(failureBuilder)).create(provider);

        assertTrue(failures.isEmpty());
        assertEquals(69, info.data().length());
    }

    @org.junit.Test
    public void testFailureNull() throws Exception {
        var provider = ObjectProviderProvider.fromJsonFile("completebasic/infoF1.json");

        new InfoFactory(MonadFactory.on(failureBuilder)).create(provider);

        assertEquals(1, failures.size());
        assertEquals(1, failures.stream().filter(failure -> failure.getCode().equals("data.nullValue")).count());
    }

    @org.junit.Test
    public void testFailureTooLong() throws Exception {
        var provider = ObjectProviderProvider.fromJsonFile("completebasic/infoF2.json");

        new InfoFactory(MonadFactory.on(failureBuilder)).create(provider);

        assertEquals(1, failures.size());
        assertEquals(1, failures.stream().filter(failure -> failure.getCode().equals("data.longData")).count());
    }

}
