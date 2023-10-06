package com.example.mockmvcintframework.testsupport;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.CollectionUtils.isEmpty;

@Data
@Builder
public class MvcRequest {
    @NonNull
    private HttpMethod method;
    @NonNull
    private String path;
    @Builder.Default
    private MediaType contentType = APPLICATION_JSON;
    private Object content;
    private Headers headers;
    private Parameters parameters;

    private boolean isResultPrinted;

    public HttpHeaders getHeaders() {
        return (headers == null) ? null : headers.getHeaders();
    }

    public MultiValueMap<String, String> getParameters() {
        return (parameters == null) ? null : parameters.getParameters();
    }

    @SuppressWarnings("unused")
    public static class MvcRequestBuilder {
        public MvcRequestBuilder httpGet(String path) {
            return method(GET).path(path);
        }

        public MvcRequestBuilder httpPost(String path, Object payload) {
            return method(POST).path(path).content(payload);
        }

        public MvcRequestBuilder httpPut(String path, Object payload) {
            return method(PUT).path(path).content(payload);
        }

        public MvcRequestBuilder httpDelete(String path) {
            return method(DELETE).path(path);
        }

        public MvcRequest buildAndPrint() {
            return isResultPrinted(true).build();
        }
    }

    @Data
    @Builder
    public static class Headers {
        private HttpHeaders headers;

        @SuppressWarnings("unused")
        public static class HeadersBuilder {
            public HeadersBuilder add(String key, String value) {
                if (isEmpty(headers)) {
                    headers = new HttpHeaders();
                }

                headers.put(key, singletonList(value));

                return this;
            }
        }
    }

    @Data
    @Builder
    public static class Parameters {
        private MultiValueMap<String, String> parameters;

        @SuppressWarnings("unused")
        public static class ParametersBuilder {
            public ParametersBuilder add(String key, String value) {
                if (isEmpty(parameters)) {
                    parameters = new LinkedMultiValueMap<>();
                }

                parameters.add(key, value);

                return this;
            }
        }
    }
}
