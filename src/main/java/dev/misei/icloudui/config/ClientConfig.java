package dev.misei.icloudui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.function.Consumer;

@Configuration
public class ClientConfig {

    public static final String BASE_URL = "https://www.icloud.com";

    @Bean
    public CookieManager cookieManager(CookieStore cookieStore) {
        return new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
    }

    @Bean
    public HttpClient httpClient(CookieManager cookieManager) {
        return HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    @Bean
    public Consumer<HttpRequest.Builder> defaultHeaders() {
        return builder -> builder
                .header("Origin", BASE_URL)
                .header("Referer", BASE_URL + "/")
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0");
    }

    @Bean
    public HttpRequestFactory httpRequestFactory(Consumer<HttpRequest.Builder> defaultHeaders, ObjectMapper objectMapper) {
        return new HttpRequestFactory(defaultHeaders, objectMapper);
    }
}


