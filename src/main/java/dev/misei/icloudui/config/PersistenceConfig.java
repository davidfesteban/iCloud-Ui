package dev.misei.icloudui.config;
import dev.misei.icloudui.model.FileBackedCookieStore;
import dev.misei.icloudui.model.FileRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.CookieStore;

@Configuration
public class PersistenceConfig {

    private static final String COOKIE_STORE_PATH = "/var/lib/icloud/cookies.json";
    private static final String REGISTRY_PATH = "/var/lib/icloud/registry.json";

    @Bean
    public CookieStore cookieStore() {
        File file = new File(COOKIE_STORE_PATH);
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            return new FileBackedCookieStore(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize persistent CookieStore", e);
        }
    }

    @Bean
    public FileRegistry fileRegistry() {
        File file = new File(REGISTRY_PATH);
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            return new FileRegistry(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize FileRegistry", e);
        }
    }
}
