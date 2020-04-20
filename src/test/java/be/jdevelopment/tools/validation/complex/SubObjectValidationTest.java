package be.jdevelopment.tools.validation.complex;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.error.Failure;
import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.error.VMonad;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
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
        failureBuilder = errorCode -> failures.add(() -> errorCode);
    }

    @Test
    public void should_validateProvider_givenValid() throws Exception {

        String json = "{\"emailAddresses\":[\"hello@world\", \"a@b\"],\"address\":{\"postalCode\":\"5030\",\"street\":\"second street\"}}";
        JsonNode node = new ObjectMapper().readTree(json);
        ObjectProvider provider = fromJsonNode(node);

        Person person = new PersonBuilder(provider, VMonad.on(failureBuilder)).build();

        assertTrue(failures.isEmpty());
        assertEquals(2, person.emailAddresses.length);
        assertEquals("hello@world", person.emailAddresses[0]);
        assertEquals("a@b", person.emailAddresses[1]);
        assertEquals("5030", person.address.postalCode);
        assertEquals("second street", person.address.street);
    }

    @Test
    public void should_invalidateProvider_givenNoAddress() throws Exception {

        String json = "{\"emailAddresses\":[\"hello@world\", \"not_a_mail_address\"]}";
        JsonNode node = new ObjectMapper().readTree(json);
        ObjectProvider provider = fromJsonNode(node);

        new PersonBuilder(provider, VMonad.on(failureBuilder)).build();

        assertEquals(2, failures.size());
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("address.required"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("emailAddresses[1].format"::equals));
    }

    @Test
    public void should_invalidateProvider_givenInvalidAddressInfo() throws Exception {

        String json = "{\"emailAddresses\":[\"hello@world\", \"a@b\"],\"address\":{\"postalCode\":\"not_ok\"}}";
        JsonNode node = new ObjectMapper().readTree(json);
        ObjectProvider provider = fromJsonNode(node);

        new PersonBuilder(provider, VMonad.on(failureBuilder)).build();

        assertEquals(2, failures.size());
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("address.postalCode.format"::equals));
        assertTrue(failures.stream().map(Failure::getCode).anyMatch("address.street.required"::equals));
    }

    private static ObjectProvider fromJsonNode(JsonNode node) {
        return property -> {
            JsonNode it = node.get(property.getName());
            if (it instanceof TextNode) return it.asText();
            if (it instanceof ArrayNode)
                return StreamSupport.stream(((Iterable<JsonNode>) it::elements).spliterator(), false)
                    .map(JsonNode::asText).toArray(String[]::new);
            if (!(it instanceof ValueNode) && it != null) return fromJsonNode(it);

            return it;
        };
    }

}
