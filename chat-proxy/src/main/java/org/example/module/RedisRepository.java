package org.example.module;

import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String,Object> redisTemplate;
    private final SetOperations<String,Object> setOperations;

//    private final RedisAsyncCommands<String, String> redisAsyncCommands;

//    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

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

    public Object findByHash(String key,String subKey){
        return redisTemplate.opsForHash().get(key,subKey);
    }

    public boolean existsInSet(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

}
