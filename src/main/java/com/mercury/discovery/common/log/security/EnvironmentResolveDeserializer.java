package com.mercury.discovery.common.log.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.mercury.discovery.utils.ContextUtils;

import java.io.IOException;

public class EnvironmentResolveDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return ContextUtils.getEnvironment().resolveRequiredPlaceholders(jsonParser.getText());
    }

}
