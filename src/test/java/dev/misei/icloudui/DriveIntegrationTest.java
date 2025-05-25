package dev.misei.icloudui;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DriveIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private DriveService driveService;

    @Test
    public void fullLoginAndListRootFiles() throws Exception {
        String appleId = System.getenv("ICLOUD_APPLE_ID");
        String password = System.getenv("ICLOUD_APPLE_PASSWORD");

        boolean loginSuccess = authService.login(appleId, password);
        if (!loginSuccess) {
            System.err.println("Login failed or 2FA required.");
            return;
        }

        JsonNode rootFiles = driveService.listRootFiles();
        System.out.println("Root iCloud Drive files:");
        System.out.println(rootFiles.toPrettyString());
    }
}
