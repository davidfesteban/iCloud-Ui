package dev.misei.icloudui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.icloudui.config.HttpRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class DriveService {

    private static final String DRIVE_ROOT_URL = "https://pXX-drivews.icloud.com/co/initialization";
    private static final String FILE_CONTENT_URL_TEMPLATE = "https://pXX-ckdatabasews.icloud.com/db/RECORD_NAME?dsid=%s";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final HttpRequestFactory httpRequestFactory;

    public JsonNode listRootFiles() throws IOException, InterruptedException {
        HttpRequest request = httpRequestFactory.build(DRIVE_ROOT_URL, "GET", null);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }

    public byte[] downloadFile(String fileId, String dsid) throws IOException, InterruptedException {
        String fileUrl = String.format(FILE_CONTENT_URL_TEMPLATE, dsid).replace("RECORD_NAME", fileId);
        HttpRequest request = httpRequestFactory.build(fileUrl, "GET", null);
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return response.body();
    }
}
