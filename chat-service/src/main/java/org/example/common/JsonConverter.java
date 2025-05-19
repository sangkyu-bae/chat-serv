package org.example.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public Map<String,String> toMap(Object obj){
        return objectMapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
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
    public <T> List<T> toObjectList(String json, Class<T> clazz) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    public <T> T toObject(Object obj, Class<T> clazz) {
        if (clazz.isInstance(obj)) {
            return (T) obj;
        } else {
            throw new ClassCastException("Object cannot be cast to " + clazz.getName());
        }
    }
}
