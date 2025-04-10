package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.exceptions.CustomGenericRuntimeException;
import kc.wheremybuckgoes.interfaces.GenAiConfig;
import kc.wheremybuckgoes.modal.AuditExternalRequest;
import kc.wheremybuckgoes.utils.NotificationGotifyHelper;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class GeminiAiService extends GenAiConfig {

    @Value("${genai.gemini.API}")
    private String key;

    @Value("${genai.gemini.endPoint}")
    private String endPoint;

    @Value("${genai.gemini.model}")
    private String model;

    // Temporary will be moved to scheduler
    private final NotificationGotifyHelper gotify;

    private final AuditExternalRequestService auditService;

    public String makeRequest(String userText){
        log.info("GeminiAiService: makeRequest");
        this.setApiKey(this.key);
        this.setEndPoint(this.endPoint);
        this.setModel(this.model);
        this.setUser(userText);
        try {
            auditService.saveAudit(AuditExternalRequest.builder().date(System.currentTimeMillis()).type(ApplicationConstant.TaskType.GenAi).message("GEMINI AI CALL").build());
            gotify.sendNotification("Gemini Api request made with text","API request made", 5);
            String response = this.makeCall();
            return this.parseResponse(response);
        } catch (Exception e) {
            log.info("ERROR: GeminiAiService: makeRequest{}", String.valueOf(e));
            throw new CustomGenericRuntimeException(ApplicationConstant.Exceptions.GEMINI_CALL_ERROR, e);
        }
    }

    private String parseResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);

        if (jsonResponse.has("candidates")) {
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (!candidates.isEmpty()) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                if (firstCandidate.has("content")) {
                    JSONObject content = firstCandidate.getJSONObject("content");
                    if (content.has("parts")) {
                        JSONArray parts = content.getJSONArray("parts");
                        if (!parts.isEmpty()) {
                            JSONObject firstPart = parts.getJSONObject(0);
                            if (firstPart.has("text")) {
                                String text = firstPart.getString("text");
                                return text.replace("```json", "").replace("```", "").trim();
                            }
                        }
                    }
                }
            }
        }
        return "";
    }
}
