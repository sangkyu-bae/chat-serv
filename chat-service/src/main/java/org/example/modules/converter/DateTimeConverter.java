package org.example.modules.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeConverter {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getBasicNow(){
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getToString14(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    public static String getToString14HHMI(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public static String getToString8HHMI(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getToString6(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
    }
    public static String getToStringHHMI(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
    }


    public static LocalDateTime toLocalDate(String date) {
        try {
            return LocalDateTime.parse(date, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. 입력값: " + date, e);
        }
    }

    public static double toNowTimeByDouble(){
        return System.currentTimeMillis();
    }
}
