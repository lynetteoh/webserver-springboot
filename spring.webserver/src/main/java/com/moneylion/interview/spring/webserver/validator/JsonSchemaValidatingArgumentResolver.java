package com.moneylion.interview.spring.webserver.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.joining;

@Component
public class JsonSchemaValidatingArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourcePatternResolver;
    private final Map<String, JsonSchema> schemaCache;

    public JsonSchemaValidatingArgumentResolver(ObjectMapper objectMapper, ResourcePatternResolver resourcePatternResolver) {
        this.objectMapper = objectMapper;
        this.resourcePatternResolver = resourcePatternResolver;
        this.schemaCache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(ValidJson.class) != null;
    }

    private String getJsonPayload(NativeWebRequest nativeWebRequest) throws IOException {
        HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        return StreamUtils.copyToString(httpServletRequest.getInputStream(), StandardCharsets.UTF_8);
    }

    private JsonSchema getJsonSchema(String schemaPath) {
        return schemaCache.computeIfAbsent(schemaPath, path -> {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
//            URL resource = JsonSchemaValidatingArgumentResolver.class.getClassLoader().getResource(path);

            if (stream == null) {
                throw new JsonSchemaException("Schema file does not exist : " + path);
            }

            Reader reader = new InputStreamReader(stream);

            // retrieve jsonSchema
            try {
                String schema = FileCopyUtils.copyToString(reader);
                JsonNode schemaNode = objectMapper.readTree(schema);
                return JsonSchemaFactory.getInstance(SpecVersionDetector.detect(schemaNode)).getSchema(schemaNode);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse provided schema: " + schemaPath, e);
            }
        });
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // get schema path from ValidJson annotation
        String schemaPath = Objects.requireNonNull(methodParameter.getParameterAnnotation(ValidJson.class)).value();

        // get JsonSchema from schemaPath
        JsonSchema schema = getJsonSchema(schemaPath);

        // parse json payload
        JsonNode json = objectMapper.readTree(getJsonPayload(nativeWebRequest));

        // do actual validation
        Set<ValidationMessage> validationResult = schema.validate(json);

        if (validationResult.isEmpty()) {
            // no validation errors, convert JsonNode to method parameter type and return it
            return objectMapper.treeToValue(json, methodParameter.getParameterType());
        } else {
            // throw exception if validation failed
            // validation error
            String errorMessages = validationResult.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(joining(" # "));
            throw new JsonSchemaException(errorMessages);
        }
    }
}
