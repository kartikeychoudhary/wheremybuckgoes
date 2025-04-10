package kc.wheremybuckgoes.interfaces;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Data
@Slf4j
public abstract class GenAiConfig {
    private String endPoint;
    private String apiKey;
    private String user;
    private String model;

    protected String makeCall() throws IOException {
        log.info("GenAiConfig: makeCall() started");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");        
        String body;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("response.json")) {
            if (inputStream == null) {
                log.error("Resource file 'response.json' not found!");
                throw new IOException("Resource file 'response.json' not found!");
            }
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        this.user = this.user.replaceAll("[^a-zA-Z0-9., ]", "");
        body = body.replace("INSERT_INPUT_HERE", this.user);
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request request = new Request.Builder()
                .url(endPoint + model + ":generateContent?key=" + apiKey)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if(response.body() != null){
            return response.body().string();
        }
        return "";
    }
}
