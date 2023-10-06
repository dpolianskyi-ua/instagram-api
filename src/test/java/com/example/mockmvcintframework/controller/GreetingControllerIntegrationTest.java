package com.example.mockmvcintframework.controller;

import com.example.mockmvcintframework.testsupport.MockMvcTestBase;
import com.example.mockmvcintframework.testsupport.MvcRequest;
import com.example.mockmvcintframework.testsupport.MvcRequest.Parameters;
import com.example.mockmvcintframework.testsupport.StringResource;
import org.junit.jupiter.api.Test;

import java.util.List;

class GreetingControllerIntegrationTest extends MockMvcTestBase {

    private final List<String> idModifiers = List.of("id");

    @Test
    void greeting_whenNoParam_returnsDefaultResponse(@StringResource("mock/default-greet.json") String expected) {
        var request = MvcRequest.builder().httpGet("/api/greeting").build();

        var actual = getResultWithStatus(request, 200);

        assertJsonEquals(expected, actual, idModifiers);
    }

    @Test
    void greeting_whenRequestParamIsProvided_returnsCustomResponse(
            @StringResource("mock/custom-greet.json") String expected) {
        var request = MvcRequest.builder()
                .httpGet("/api/greeting")
                .parameters(Parameters.builder().add("name", "Custom User").build())
                .build();

        var actual = getResultWithStatus(request, 200);

        assertJsonEquals(expected, actual, idModifiers);
    }
}