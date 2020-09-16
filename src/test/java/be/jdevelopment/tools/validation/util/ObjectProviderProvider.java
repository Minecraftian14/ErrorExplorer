package be.jdevelopment.tools.validation.util;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.complex.SubObjectValidationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.StreamSupport;

public class ObjectProviderProvider {

    /**
     * Reading the json file data to a {@link JsonNode}
     * Then returning an {@link ObjectProvider}
     * by calling fromJsonNode({@link JsonNode})
     */
    public static ObjectProvider fromJsonFile(String path) throws IOException {
        JsonNode node;
        try (InputStream inputStream = SubObjectValidationTest.class.getClassLoader().getResourceAsStream(path)) {
            node = new ObjectMapper().readTree(inputStream);
        }
        return fromJsonNode(node);
    }

    /**
     * <p>
     * This method will return an {@link ObjectProvider}
     * ObjectProvider#provideFor(PropertyToken)
     * will return the respective value off the {@link JsonNode}
     * </p>
     */
    private static ObjectProvider fromJsonNode(JsonNode node) {
        return property -> {
            JsonNode it = node.get(property.getName());
            if (it == null) return null;

            return mapping(it);
        };
    }

    /**
     * <p>
     * A Object Type mapping.
     * Basically, returning the suitable object
     * as suited by the contents of {@link JsonNode}
     * <p>
     * It maps 'null', 'String, 'int', 'double', 'array' and a 'ValueNode' itself
     * </p>
     */
    private static Object mapping(JsonNode node) {
        if (node.isNull()) return null;
        if (node.isTextual()) return node.asText();
        if (node.isInt()) return node.asInt();
        if (node.isNumber()) return node.asDouble();
        if (node.isArray())
            return StreamSupport.stream(((Iterable<JsonNode>) node::elements).spliterator(), false)
                    .map(ObjectProviderProvider::mapping)
                    .toArray(Object[]::new);
        if (!(node instanceof ValueNode)) return fromJsonNode(node);

        return node;
    }

}
