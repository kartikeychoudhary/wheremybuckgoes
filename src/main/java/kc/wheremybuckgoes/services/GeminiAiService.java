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

    // Temporary will be moved to scheduler
    private final NotificationGotifyHelper gotify;

    private final AuditExternalRequestService auditService;

    String requestBody = """
            {
                                            "contents": [
                                              {
                                                "role": "user",
                                                "parts": [
                                                  {
                                                    "text": "**System Instruction:** %s             User Input: %s"
                                                  }
                                                ]
                                              }
                                            ],
                                            "generationConfig": {
                                              "temperature": 1,
                                              "topK": 0,
                                              "topP": 0.95,
                                              "maxOutputTokens": 8192,
                                              "stopSequences": []
                                            },
                                            "safetySettings": [
                                              {
                                                "category": "HARM_CATEGORY_HARASSMENT",
                                                "threshold": "BLOCK_MEDIUM_AND_ABOVE"
                                              },
                                              {
                                                "category": "HARM_CATEGORY_HATE_SPEECH",
                                                "threshold": "BLOCK_MEDIUM_AND_ABOVE"
                                              },
                                              {
                                                "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                                                "threshold": "BLOCK_MEDIUM_AND_ABOVE"
                                              },
                                              {
                                                "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                                                "threshold": "BLOCK_MEDIUM_AND_ABOVE"
                                              }
                                            ]
                                          }
        """;

    private String system = "Provided a sms \\\"Debit\\nINR 1006.00\\nA/c no. XX1234\\n11-11-23 18:39:26\\nUPI/P2M/1231236611/Add Money to Wallet\\nSMS BLOCKUPI Cust ID to 9199518602, if not you - Axis Bank\\\"\\n\\nconvert it to json with keys such as \\\"price\\\", \\\"type\\\", \\\"mode\\\", \\\"date\\\", \\\"account\\\", \\\"accountType\\\", \\\"spendAt\\\", \\\"accountName\\\"   (accountType can be credit card/ debit card or bank)\\nso the output for above input would be\\n\\n{\\n\\\"price\\\":\\\"1006.00\\\",\\n\\\"type\\\":\\\"Debit\\\",\\n\\\"account\\\":\\\"XX1234\\\",\\n\\\"date\\\":\\\"11-11-23 18:39:26\\\",\\n\\\"mode\\\":\\\"UPI\\\",\\n\\\"accountType\\\":\\\"Bank\\\",\\n\\\"accountName\\\":\\\"Axis\\\"\\n\\\"spendAt\\\":\\\"Wallet\\\"\\n}\\n\\nexample 2\\nINR 605.00 spent on ICICI Bank Card XX1005 on 23-Apr-24 at PUNE AUTOMOBILE. Avl Lmt: INR 5,726.12. To dispute,call 18002662/SMS BLOCK 1005 to 9215876766\\n{\\n\\\"price\\\":\\\"605.00\\\",\\n\\\"type\\\":\\\"Debit\\\",\\n\\\"account\\\":\\\"XX1005\\\",\\n\\\"date\\\":\\\"123-04-24\\\",\\n\\\"mode\\\":\\\"CARD\\\"\\n\\\"accountType\\\":\\\"Card\\\",\\n\\\"accountName\\\":\\\"ICICI\\\"\\n\\\"spendAt\\\":\\\"PUNE AUTOMOBILE\\\"\\n output should be a valid JSON";
    @Override
    protected String getJSONBody(String system, String user) {
        return requestBody.formatted(system, user.strip());
    }

    public String makeRequest(String userText){
        log.info("GeminiAiService: makeRequest");
        this.setApiKey(this.key);
        this.setEndPoint(this.endPoint);
        this.setSystem(this.system);
        this.setUser(userText);
        try {
            auditService.saveAudit(AuditExternalRequest.builder().date(System.currentTimeMillis()).type(ApplicationConstant.TaskType.GenAi).message("GEMINI AI CALL").build());
            gotify.sendNotification("Gemini Api request made with text","API request made", 5);
            String response = this.makeCall();
            JSONObject json = new JSONObject(response);
            String result = "";
            JSONArray jsonArray = json.getJSONArray("candidates");
            if(!jsonArray.isEmpty()){
                JSONObject content = jsonArray.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if(!parts.isEmpty()){
                    result = parts.getJSONObject(0).getString("text");
                }
            }
            return result;
        } catch (Exception e) {
            throw new CustomGenericRuntimeException(ApplicationConstant.Exceptions.GEMINI_CALL_ERROR, e);
        }
    }
}
