package br.com.uniproof.integration.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

public class FormUtils {

    public static JsonNode merge(Object mainNode, Object updateNode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return FormUtils.merge(mapper.writeValueAsString(mainNode), mapper.writeValueAsString(updateNode));
    }

    public static JsonNode merge(String mainNode, String updateNode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode origemObj = mapper.readTree(mainNode);
        JsonNode overwriteObj = mapper.readTree(updateNode);
        return FormUtils.merge(origemObj, overwriteObj);
    }

    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {

            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                merge(jsonNode, updateNode.get(fieldName));
            } else {
                if (mainNode instanceof ObjectNode) {
                    // Overwrite field
                    JsonNode value = updateNode.get(fieldName);
                    ((ObjectNode) mainNode).put(fieldName, value);
                }
            }
        }
        return mainNode;
    }
}
