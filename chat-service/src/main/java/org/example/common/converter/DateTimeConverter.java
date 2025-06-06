package org.example.common.converter;

import java.time.LocalDateTime;
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


}
