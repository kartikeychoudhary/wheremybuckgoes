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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
                if(key1.toLowerCase().contains("sept") || key2.toLowerCase().contains("sept")){
                    key1 = key1.replace("Sept", "Sep");
                    key2 = key2.replace("Sept", "Sep");
                    return this.compare(key1, key2);
                }
                return 0;
            }
        }
    }


    public static Long convertDateToMillis(String date, String format){
        try {
            // datetime formatter for format 01 Jan 2025
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate localDate = LocalDate.parse(date, formatter);
            return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        } catch (Exception e) {
            log.info("Exception occurred while parsing date value -> " + date);
            return null;
        }
    }

    public static Long convertDateToMillis(String date, String[] formats){
        for (String format : formats) {
            Long d = convertDateToMillis(date, format);
            if(d != null){
                return d;
            }
        }
        return null;
    }

    public static Long getThisWeekTimeInMillis(DayOfWeek dayOfWeek) {
        LocalDate today = LocalDate.now();
        LocalDate thisWeeksMonday = today.with(TemporalAdjusters.previousOrSame(dayOfWeek));
        ZonedDateTime startOfThisWeeksMonday = thisWeeksMonday.atStartOfDay(ZoneId.systemDefault());
        return startOfThisWeeksMonday.toInstant().toEpochMilli();
    }

    public static Long getStartOfCurrentMonthMillis() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        ZonedDateTime startOfThisMonth = firstDayOfThisMonth.atStartOfDay(ZoneId.systemDefault());
        return startOfThisMonth.toInstant().toEpochMilli();
    }

    public static Long getStartOfCurrentQuarterMillis() {
        LocalDate today = LocalDate.now();
        Month currentMonth = today.getMonth();
        Month startOfQuarterMonth;
        if (currentMonth.getValue() >= Month.JANUARY.getValue() && currentMonth.getValue() <= Month.MARCH.getValue()) {
            startOfQuarterMonth = Month.JANUARY;
        } else if (currentMonth.getValue() >= Month.APRIL.getValue() && currentMonth.getValue() <= Month.JUNE.getValue()) {
            startOfQuarterMonth = Month.APRIL;
        } else if (currentMonth.getValue() >= Month.JULY.getValue() && currentMonth.getValue() <= Month.SEPTEMBER.getValue()) {
            startOfQuarterMonth = Month.JULY;
        } else {
            startOfQuarterMonth = Month.OCTOBER;
        }
        LocalDate firstDayOfCurrentQuarter = LocalDate.of(today.getYear(), startOfQuarterMonth, 1);
        ZonedDateTime startOfCurrentQuarter = firstDayOfCurrentQuarter.atStartOfDay(ZoneId.systemDefault());
        return startOfCurrentQuarter.toInstant().toEpochMilli();
    }

    public static Long getStartOfCurrentYearMillis() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        LocalDate firstDayOfCurrentYear = LocalDate.of(currentYear, Month.JANUARY, 1);
        ZonedDateTime startOfCurrentYear = firstDayOfCurrentYear.atStartOfDay(ZoneId.systemDefault());
        return startOfCurrentYear.toInstant().toEpochMilli();
    }

    public static Long getStartOfLastYearMillis() {
        LocalDate today = LocalDate.now();
        int lastYear = today.getYear() - 1;
        LocalDate firstDayOfLastYear = LocalDate.of(lastYear, Month.JANUARY, 1);
        ZonedDateTime startOfLastYear = firstDayOfLastYear.atStartOfDay(ZoneId.systemDefault());
        return startOfLastYear.toInstant().toEpochMilli();
    }

    /**
     * Converts milliseconds since the epoch to a formatted date string.
     *
     * @param millis The milliseconds since the epoch.
     * @param format The desired format string. Can be a standard date pattern
     * (e.g., "dd MMM yy", "MMM yy", "yyyy") or "quarter".
     * @return The formatted date string, or an error message if the format is invalid.
     */
    public static String getDateFromMillis(long millis, String format) {
        try {
            // Convert milliseconds to ZonedDateTime (using system default time zone)
            Instant instant = Instant.ofEpochMilli(millis);
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

            if ("quarter".equalsIgnoreCase(format)) {
                // Handle the "quarter" format
                Month currentMonth = zonedDateTime.getMonth();
                int year = zonedDateTime.getYear() % 100; // Get last two digits of the year

                Month startMonth;
                Month endMonth;

                if (currentMonth.getValue() >= Month.JANUARY.getValue() && currentMonth.getValue() <= Month.MARCH.getValue()) {
                    startMonth = Month.JANUARY;
                    endMonth = Month.MARCH;
                } else if (currentMonth.getValue() >= Month.APRIL.getValue() && currentMonth.getValue() <= Month.JUNE.getValue()) {
                    startMonth = Month.APRIL;
                    endMonth = Month.JUNE;
                } else if (currentMonth.getValue() >= Month.JULY.getValue() && currentMonth.getValue() <= Month.SEPTEMBER.getValue()) {
                    startMonth = Month.JULY;
                    endMonth = Month.SEPTEMBER;
                } else {
                    startMonth = Month.OCTOBER;
                    endMonth = Month.DECEMBER;
                }

                // Format the output string for the quarter
                String startMonthAbbrev = startMonth.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);
                String endMonthAbbrev = endMonth.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);

                return startMonthAbbrev + "-" + endMonthAbbrev + " " + String.format("%02d", year);

            } else {
                // Handle standard date formats
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return zonedDateTime.format(formatter);
            }
        } catch (IllegalArgumentException e) {
            // Catch exceptions related to invalid date format patterns
            return "Error: Invalid date format pattern - " + format;
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            return "Error converting milliseconds to date: " + e.getMessage();
        }
    }
}
