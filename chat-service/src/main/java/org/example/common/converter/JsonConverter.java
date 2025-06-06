package org.example.common.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonConverter {
    private final ObjectMapper objectMapper;

    public String toJson(Object obj) {
        String json = "";
        try {
           json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

//    public Map<String,String> toMap(Object obj){
//        return objectMapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
//    }

    public Map<String, String> toMap(Object obj) {
        Map<String, Object> tempMap = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "null");
        }
        return result;
    }


    public Map<String,String> toMap(String str){
        try {
            return objectMapper.readValue((String) str, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }

    public String toJson(String str) throws JsonProcessingException {
        return objectMapper.writeValueAsString(str);
    }
    public Map<String,Object> toObjectMap(String str) {
        try {
            return objectMapper.readValue(str, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }


}
