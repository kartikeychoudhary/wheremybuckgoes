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
        log.info("GenAiConfig: makeCall()");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        // use response.json file to get the body present at resorces/response.json
        String body = new String(Files.readAllBytes(Paths.get("src/main/resources/response.json")));
        // replace the "INSERT_INPUT_HERE" with this.user
        // sanitize the user text which will invalid the body json
        // use java built in function to sanitize the user text . , are allowed
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
