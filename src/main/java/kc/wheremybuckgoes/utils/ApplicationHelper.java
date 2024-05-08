package kc.wheremybuckgoes.utils;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.response.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
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

    public static Map<Integer, String> getMonthsIndexMap(){
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < ApplicationConstant.MONTHS.length; i++) {
            map.put(i, ApplicationConstant.MONTHS[i]);
        }
        return map;
    }

    public static SortedMap<String, long[]> sortByMonthYear(Map<String, long[]> fromMap) {
        SortedMap<String, long[]> sortedMap = new TreeMap<>(new MonthYearComparator());
        sortedMap.putAll(fromMap);
        return sortedMap;
    }

    private static class MonthYearComparator implements Comparator<String> {

        private final SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");

        @Override
        public int compare(String key1, String key2) {
            try {
                Date date1 = formatter.parse(key1);
                Date date2 = formatter.parse(key2);
                return date1.compareTo(date2);
            } catch (ParseException e) {
                log.info("Exception occurred while parsing dates value -> Date1 : " + key1 + " Date2 : " + key2);
                return 0;
            }
        }
    }

}
