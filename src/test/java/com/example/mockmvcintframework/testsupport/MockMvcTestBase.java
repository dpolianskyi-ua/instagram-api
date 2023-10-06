package com.example.mockmvcintframework.testsupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.CollectionUtils.isEmpty;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class MockMvcTestBase extends SpringTestBase {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SneakyThrows
    public String getResultWithStatus(MvcRequest mvcRequest, int statusCode) {
        var resultActions = perform(mvcRequest).andExpect(status().is(statusCode));

        return (mvcRequest.isResultPrinted())
                ? resultActions.andDo(print()).andReturn().getResponse().getContentAsString()
                : resultActions.andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    public void assertJsonEquals(String expected, String actual, List<String> modifiers) {
        var config = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();

        var context = JsonPath.using(config).parse(expected);

        if (!isEmpty(modifiers)) {
            modifiers.forEach(m -> context.set(m, JsonPath.read(actual, m)));
        }

        assertJsonEquals(context.jsonString(), actual);
    }

    @SneakyThrows
    public void assertJsonEquals(String expected, String actual) {
        JSONAssert.assertEquals(expected, actual, true);
    }

    @SneakyThrows
    private ResultActions perform(MvcRequest request) {
        var headers = request.getHeaders();
        var parameters = request.getParameters();
        var content = request.getContent();

        var requestBuilder = request(request.getMethod(), request.getPath());

        if (!isNull(headers) && !headers.isEmpty()) {
            requestBuilder.headers(headers);
        }

        if (!isNull(parameters) && !isEmpty(parameters)) {
            requestBuilder.queryParams(parameters);
        }

        if (List.of(POST, PUT).contains(request.getMethod()) && !isNull(content)) {
            requestBuilder.content(objectMapper.writeValueAsString(content));
        }

        return mockMvc.perform(requestBuilder.contentType(request.getContentType()));
    }
}
