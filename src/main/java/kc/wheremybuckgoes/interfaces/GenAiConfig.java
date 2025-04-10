package kc.wheremybuckgoes.interfaces;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        log.info("GenAiConfig: makeCall() read body response.json start");
        String body = new String(Files.readAllBytes(Paths.get("src/main/resources/response.json")));
        this.user = this.user.replaceAll("[^a-zA-Z0-9., ]", "");
        body = body.replace("INSERT_INPUT_HERE", this.user);
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request request = new Request.Builder()
                .url(endPoint + model + ":generateContent?key=" + apiKey)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        log.info("GenAiConfig: makeCall() for :{}", String.valueOf(this.user));
        Response response = client.newCall(request).execute();
        if(response.body() != null){
            return response.body().string();
        }
        return "";
    }
}
