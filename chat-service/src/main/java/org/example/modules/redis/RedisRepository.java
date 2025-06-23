package org.example.modules.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import org.example.domain.ChatMessage;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String,Object> redisTemplate;
    private final SetOperations<String,Object> setOperations;

    private final RedisAsyncCommands<String, String> redisAsyncCommands;

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public void setTypeSave(String key, String value){
        try{
            setOperations.add(key,value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Set<Object> findWithSetTypeByAll(String key){
        try{
            return setOperations.members(key);
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Set<String> findWithSetTypeByAllToString(String key) {
        Set<Object> set = findWithSetTypeByAll(key);
        if (set == null) {
            return Collections.emptySet();
        }

        return set.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public void listTypeSave(String key, String value){
        try{
            redisTemplate.opsForList().rightPush(key,value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Object> findWithListType(String key, long start, long end){
        RedisOperations<String, Object> operations = redisTemplate.opsForList().getOperations();
        try{
            return operations.opsForList().range(key,start,end);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public List<Object> findWithListTypeByAll(String key){
        try{
            return findWithListType(key,0,-1);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsInSet(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }


    public Mono<Boolean> saveWhitList(String key, String value) {
        return reactiveRedisTemplate.opsForList()
                .rightPush(key, value)
                .map(count -> true);
    }

    public Flux<String> findByKey(String key, long start, long end) {
        return reactiveRedisTemplate.opsForList()
                .range(key, start, end)
                .flatMap(json -> {
                    return Mono.just(json);
                });
    }

    public Mono<Boolean> saveWithHash(String key, Map<String,String> info) {
        return reactiveRedisTemplate.opsForHash()
                .putAll(key, info);
    }

    public Mono<Map<String, String>> findByHash(String key) {
        return reactiveRedisTemplate.opsForHash()
                .entries(key)
                .collectMap(
                        entry -> (String) entry.getKey(),
                        entry -> (String) entry.getValue()
                );
    }

    public Mono<Boolean> deleteHash(String key) {
        return reactiveRedisTemplate.delete(key)
                .map(deletedCount -> deletedCount > 0);
    }

//    public Mono<Long> addToSet(String key, String value) {
//        return reactiveRedisTemplate.opsForSet().add(key, value);
//    }
    public Mono<Long> addToSet(String key, String value) {
        return reactiveRedisTemplate.opsForSet().add(key, value);
    }

    public Flux<String> findBySet(String key){
        return  reactiveRedisTemplate.opsForSet()
                .members(key);
    }

    public Mono<Set<String>> getAllSetMembersAsSet(String key) {
        return reactiveRedisTemplate.opsForSet()
                .members(key) // Flux<String>
                .collect(Collectors.toSet()); // Mono<Set<String>>
    }

    public Mono<Long> removeFromSet(String key, String value) {
        return reactiveRedisTemplate.opsForSet().remove(key, value);
    }
}
