package cn.chuanwise.xiaoming.permission;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PermissionDeserializer extends JsonDeserializer<Permission> {
    @Override
    public Permission deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return Permission.compile(jsonParser.readValueAs(String.class));
    }
}
