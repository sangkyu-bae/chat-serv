package com.chat.user.module.common.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class DateTimeConverter {

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

    public static String toDateTimeString(int hour, int minute) {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
        return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public static String getStockStartDate() {
        return toDateTimeString(8, 29);
    }

    public static String getStockEndDate() {
        return toDateTimeString(15, 31);
    }

}
