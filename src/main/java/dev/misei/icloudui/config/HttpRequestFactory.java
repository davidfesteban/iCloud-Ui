package dev.misei.icloudui.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.function.Consumer;

public class HttpRequestFactory {

    private final Consumer<HttpRequest.Builder> defaultHeaders;
    private final ObjectMapper objectMapper;

    public HttpRequestFactory(Consumer<HttpRequest.Builder> defaultHeaders, ObjectMapper objectMapper) {
        this.defaultHeaders = defaultHeaders;
        this.objectMapper = objectMapper;
    }

    public HttpRequest build(String url, String method, Object body) throws IOException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));

        if (body != null) {
            String json = objectMapper.writeValueAsString(body);
            builder.method(method, HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        defaultHeaders.accept(builder);
        return builder.build();
    }
}
