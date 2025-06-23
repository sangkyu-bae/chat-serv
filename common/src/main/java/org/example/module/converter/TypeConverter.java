package org.example.module.converter;

import io.micrometer.common.util.StringUtils;

public class TypeConverter {

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  // 음수와 소수점도 허용
    }

    public static int toValueByInt(String val) {
        if (!isNumberAndNonBlank(val)) {
            return 0;
        }
        return Integer.parseInt(val);
    }

    public static long toValueByLong(String val) {
        if (!isNumberAndNonBlank(val)) {
            return 0;
        }
        return Long.parseLong(val);
    }

    public static double toValueByDouble(String val) {
        if (!isNumberAndNonBlank(val)) {
            return 0;
        }

        return Double.parseDouble(val);
    }

    public static boolean isNumberAndNonBlank(String val){
        if (StringUtils.isBlank(val) || !isNumeric(val)) {
            return false;
        }

        return true;
    }

    public static boolean toValueByBoolean(String val){
        if(val.equals("true")){
            return true;
        }

        return false;
    }
}
