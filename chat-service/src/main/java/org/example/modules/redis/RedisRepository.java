package org.example.modules.redis;

import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.List;
import java.util.Set;

public class RedisRepository {

    private RedisTemplate<String,Object> redisTemplate;
    private SetOperations<String,Object> setOperations;
    private RedisOperations<String, Object> operations;

    private RedisAsyncCommands<String, String> redisAsyncCommands;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.setOperations = redisTemplate.opsForSet();
        this.operations = redisTemplate.opsForList().getOperations();
    }

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

}
