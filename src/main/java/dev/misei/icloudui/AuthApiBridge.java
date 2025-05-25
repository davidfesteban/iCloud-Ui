package dev.misei.icloudui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.misei.icloudui.config.HttpRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthApiBridge {

    private static final String LOGIN_URL = "https://setup.icloud.com/setup/ws/1/login";
    private static final String VERIFY_DEVICES_URL = "https://setup.icloud.com/setup/ws/1/listDevices";
    private static final String SEND_CODE_URL = "https://setup.icloud.com/setup/ws/1/verify/trusteddevice";
    private static final String VALIDATE_CODE_URL = "https://setup.icloud.com/setup/ws/1/validate/trusteddevice";

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final HttpRequestFactory httpRequestFactory;

    public HttpResponse<String> login(String appleId, String password) throws IOException, InterruptedException {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("apple_id", appleId);
        body.put("password", password);
        body.put("extended_login", false);

        var request = httpRequestFactory.build(LOGIN_URL, "POST", body);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> listDevices() throws IOException, InterruptedException {
        var request = httpRequestFactory.build(VERIFY_DEVICES_URL, "GET", null);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendVerificationCode(String deviceId) throws IOException, InterruptedException {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("device", deviceId);
        body.put("push", true);

        var request = httpRequestFactory.build(SEND_CODE_URL, "POST", body);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> validateSecurityCode(String code) throws IOException, InterruptedException {
        ObjectNode codeNode = objectMapper.createObjectNode();
        codeNode.put("code", code);

        ObjectNode validateBody = objectMapper.createObjectNode();
        validateBody.set("securityCode", codeNode);

        var request = httpRequestFactory.build(VALIDATE_CODE_URL, "POST", validateBody);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Optional<String> getFirstSMSCapableDevice(JsonNode responseJson) {
        ArrayNode devices = (ArrayNode) responseJson.get("devices");
        if (devices == null) return Optional.empty();

        for (JsonNode device : devices) {
            if (device.has("canReceiveSMS") && device.get("canReceiveSMS").asBoolean(false)) {
                return Optional.ofNullable(device.get("id").asText());
            }
        }
        return Optional.empty();
    }
}