package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.impl.Monads;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubObjectValidationTest {

    /* Validation process */

    private HashSet<Failure> failures;
    private FailureBuilder failureBuilder;

    @Before
    public void setUp() {
        failures = new HashSet<>();
        failureBuilder = errorCode -> failures.add(errorCode::toString);
    }

    @Test
    public void should_validateProvider_givenValid() throws Exception {
        var provider = fromJsonFile("complex/givenValidPerson.json");

        Person person = new PersonFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);
        Predicate<String> mailsContains = str -> person.getAllMails().stream().anyMatch(str::equals);

        assertTrue(failures.isEmpty());
        assertEquals(2, person.getAllMails().size());
        assertTrue(mailsContains.test("hello@world"));
        assertTrue(mailsContains.test("a@b"));
        assertEquals("5030", person.address().postalCode());
        assertEquals("second street", person.address().street());
        assertEquals("hello@world", person.mails().getPreferredMail());
    }

    @Test
    public void should_invalidateProvider_givenNoAddressAndBadMail() throws Exception {
        var provider = fromJsonFile("complex/givenNoAddressAndBadMailList.json");

        new PersonFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(5, failures.size());
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("address.required"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddresses.duplicates"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddresses[1].format"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddresses[2].format"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddresses[3].type"::equals));
    }

    @Test
    public void should_invalidateProvider_givenInvalidAddressInfo() throws Exception {
        var provider = fromJsonFile("complex/givenInvalidAddressInfo.json");

        new PersonFactory(Monads.createOnFailureBuilder(failureBuilder)).create(provider);

        assertEquals(3, failures.size());
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("address.postalCode.format"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("address.street.required"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("defaultEmail.notfound"::equals));
    }

    private static ObjectProvider fromJsonFile(String path) throws IOException {
        JsonNode node;
        try(InputStream inputStream = SubObjectValidationTest.class.getClassLoader().getResourceAsStream(path)) {
            node = new ObjectMapper().readTree(inputStream);
        }
        return fromJsonNode(node);
    }

    private static ObjectProvider fromJsonNode(JsonNode node) {
        return property -> {
            JsonNode it = node.get(property.getName());
            if (it == null) return null;

            return mapping(it);
        };
    }

    private static Object mapping(JsonNode node) {
        if (node.isNull()) return null;
        if (node.isTextual()) return node.asText();
        if (node.isInt()) return node.asInt();
        if (node.isNumber()) return node.asDouble();
        if (node.isArray())
            return StreamSupport.stream(((Iterable<JsonNode>) node::elements).spliterator(), false)
                    .map(SubObjectValidationTest::mapping)
                    .toArray(Object[]::new);
        if (!(node instanceof ValueNode)) return fromJsonNode(node);

        return node;
    }

}
