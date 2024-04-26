package kc.wheremybuckgoes.utils;

import kc.wheremybuckgoes.response.GenericResponse;
import org.springframework.http.HttpStatus;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ApplicationHelper{

    private ApplicationHelper(){}

    public static <T> GenericResponse<T> mapToGenericResponse(HttpStatus status, T payload){
        Map<String, T> map = new HashMap<>();
        map.put("RESULT", payload);
        return GenericResponse
                .<T>builder()
                .status(status)
                .payload(map)
                .build();
    }

    public static <T> GenericResponse<T> mapToGenericResponse(HttpStatus status, T payload, String message){
        Map<String, T> map = new HashMap<>();
        map.put("RESULT", payload);
        return GenericResponse
                .<T>builder()
                .status(status)
                .message(message)
                .payload(map)
                .build();
    }

    public static long getTodayTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String convertToSupportedString(String inputString) {
        if (inputString == null) {
            return "";
        }
        // Attempt to decode using UTF-8 (common encoding)
        try {
            byte[] utf8Bytes = inputString.getBytes(StandardCharsets.UTF_8);
            return new String(utf8Bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Handle the exception if UTF-8 decoding fails
        }
        // If all else fails, replace unsupported characters with a placeholder
        StringBuilder supportedString = new StringBuilder();
        for (char c : inputString.toCharArray()) {
            if (Character.isDefined(c)) {
                supportedString.append(c);
            } else {
                supportedString.append('?'); // Replace with your preferred placeholder
            }
        }
        return supportedString.toString();
    }
}
