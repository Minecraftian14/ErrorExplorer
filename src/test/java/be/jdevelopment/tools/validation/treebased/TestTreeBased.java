package be.jdevelopment.tools.validation.treebased;

import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.impl.Monads;
import be.jdevelopment.tools.validation.util.ObjectProviderHelper;
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
        failureBuilder = errorCode -> failures.add(errorCode::toString);
    }

    @org.junit.Test
    public void testSuccess() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("treebased/membersValid.json");

        Member info = new MemberFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertTrue(failures.isEmpty());
        assertEquals(5, info.getDepth());
    }

    @org.junit.Test
    public void testSuccessEnormous() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("treebased/membersEnormous.json");

        Member info = new MemberFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertTrue(failures.isEmpty());
        assertEquals(21, info.getDepth());
    }

    @org.junit.Test
    public void testFailureWrongName() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("treebased/membersWrongName.json");

        Member info = new MemberFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(7, failures.size());
        assertEquals(7, failures.stream().filter(failure -> failure.getCode().endsWith("invalidCharacters")).count());
        assertEquals(5, info.getDepth());
    }

    @org.junit.Test
    public void testFailureNullElements() throws Exception {
        var provider = ObjectProviderHelper.objectProviderFromJsonFile("treebased/membersNullElements.json");

        Member info = new MemberFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(11, failures.size());
        assertEquals(11, failures.stream().filter(failure -> failure.getCode().endsWith("nullValue")).count());
        assertEquals(5, info.getDepth());
    }

}
