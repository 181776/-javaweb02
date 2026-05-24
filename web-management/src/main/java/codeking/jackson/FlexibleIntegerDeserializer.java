package codeking.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/** 兼容 salary 传字符串数字，如 "9000"。 */
public class FlexibleIntegerDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        if (t == JsonToken.VALUE_STRING) {
            String s = p.getText().trim();
            if (s.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw JsonMappingException.from(p, "Not a valid integer: " + s);
            }
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return p.getIntValue();
        }
        if (t == JsonToken.VALUE_NUMBER_FLOAT) {
            return (int) p.getDoubleValue();
        }
        throw JsonMappingException.from(p, "Cannot deserialize Integer from token " + t);
    }
}
