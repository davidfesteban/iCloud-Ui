package dev.misei.icloudui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthApiBridge authApiBridge;
    private final ObjectMapper objectMapper;

    public boolean login(String appleId, String password) throws IOException, InterruptedException {
        HttpResponse<String> loginResponse = authApiBridge.login(appleId, password);

        int status = loginResponse.statusCode();
        if (status == 200) {
            return true;
        } else if (status == 409) {
            return true; // Indicates 2FA required
        }

        return false;
    }

    public Optional<String> requestTwoFactorCode() throws IOException, InterruptedException {
        HttpResponse<String> deviceResponse = authApiBridge.listDevices();
        JsonNode responseJson = objectMapper.readTree(deviceResponse.body());

        JsonNode devicesNode = responseJson.get("devices");
        if (devicesNode != null && devicesNode.isArray()) {
            for (JsonNode device : devicesNode) {
                if (device.path("canReceiveSMS").asBoolean(false)) {
                    String deviceId = device.path("id").asText();
                    authApiBridge.sendVerificationCode(deviceId);
                    return Optional.of(deviceId);
                }
            }
        }
        return Optional.empty();
    }

    public boolean verifySecurityCode(String code) throws IOException, InterruptedException {
        HttpResponse<String> response = authApiBridge.validateSecurityCode(code);
        return response.statusCode() == 200;
    }
}
