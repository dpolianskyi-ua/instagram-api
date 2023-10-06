package com.example.mockmvcintframework.testsupport;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StringResourceParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(StringResource.class)
                && parameterContext.getParameter().getType().equals(String.class);
    }

    @Override
    @SneakyThrows
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var resourcePath = parameterContext
                .findAnnotation(StringResource.class)
                .map(StringResource::value)
                .orElseThrow();

        @Cleanup var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

        return new String(inputStream.readAllBytes(), UTF_8);
    }
}
