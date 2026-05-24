package codeking.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * 前端常把 OSS 上传结果整体塞进 image：{"url":"https://..."}。
 * 此处兼容纯字符串或对象（取 url）写入 {@link codeking.pojo.Emp#getImage()}。
 */
public class ImageUrlDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isObject() && node.hasNonNull("url")) {
            return node.get("url").asText(null);
        }
        return null;
    }
}
