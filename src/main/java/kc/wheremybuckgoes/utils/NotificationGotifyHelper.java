package kc.wheremybuckgoes.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class NotificationGotifyHelper {

    @Value("${gotify.url}")
    private String url;

    @Value("${gotify.api}")
    private String api;

    @Value("${gotify.disabled}")
    private boolean disabled;

    public void sendNotification(String message, String title, int priority) throws IOException {
        if(this.disabled){return;}
        message = ApplicationHelper.convertToSupportedString(message);
        log.info("NotificationGotifyHelper: sendNotification: " + message);
        if(url != null && api != null) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create("{\"title\":\"%s\",\"message\":\"%s\",\"priority\":%d}".formatted(title, message, priority), mediaType);
            Request request = new Request.Builder()
                    .url(url + api)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response res = client.newCall(request).execute();
            if (res.body() != null) {
                String response = res.body().string();
                log.info("NotificationGotifyHelper: sendNotification: " + response);
            }
        }
    }
}
