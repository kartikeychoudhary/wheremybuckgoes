package kc.wheremybuckgoes.interfaces;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

@Data
@Slf4j
public abstract class GenAiConfig {
    private String endPoint;
    private String apiKey;
    private String system;
    private String user;
    private String model;

    protected abstract String getJSONBody(String system, String role);
    protected String makeCall() throws IOException {
        log.info("GenAiConfig: makeCall()");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(getJSONBody(system, user), mediaType);
        Request request = new Request.Builder()
                .url(endPoint + model + ":generateContent?key=" + apiKey)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if(response.body() != null){
            return response.body().string();
        }
        return "";
    }
}
