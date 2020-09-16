package be.jdevelopment.tools.validation.treebased;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.error.impl.MonadFactory;
import be.jdevelopment.tools.validation.util.ObjectProviderProvider;
import org.junit.Before;

import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTreeBased {

    private HashSet<Failure> failures;
    private FailureBuilder failureBuilder;

    @Before
    public void setUp() {
        failures = new HashSet<>();
        failureBuilder = errorCode -> failures.add(() -> errorCode);
    }

    @org.junit.Test
    public void testSuccess() throws Exception {
        var provider = ObjectProviderProvider.fromJsonFile("treebased/membersValid.json");

        Member info = new MemberFactory(MonadFactory.on(failureBuilder)).create(provider);

        assertTrue(failures.isEmpty());
        assertEquals(5, info.getDepth());
    }

    @org.junit.Test
    public void testFailureWrongName() throws Exception {
        var provider = ObjectProviderProvider.fromJsonFile("treebased/membersWrongName.json");

        Member info = new MemberFactory(MonadFactory.on(failureBuilder)).create(provider);

        failures.forEach(failure -> System.out.println(failure.getCode()));


        assertEquals(1, failures.size());
        assertEquals(5, info.getDepth());
    }

}
