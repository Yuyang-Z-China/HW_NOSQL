package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class StrResolver implements TypeResolver {

    private MyCounters myCounters;
    RedisTemplate redisTemplate;

    @Override
    public String resolve() {
        String res = "没有操作";
        String key = myCounters.getKeyFields();
        String value = myCounters.getValueFields();
        int expireTime = myCounters.getExpireTime();
        if (key != null) {
            if (redisTemplate.hasKey(key) && redisTemplate.type(key).equals("string")) {
                if (value != null) {
                    if (expireTime != 0) {
                        redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
                        res = "键：" + key + "，键值为：" + value + "，过期时间为：" + expireTime;
                    } else {
                        redisTemplate.opsForValue().set(key, value);
                        res = "键：" + key + "，键值为：" + value;
                    }
                } else {
                    if (expireTime != 0) {
                        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                        res = "键值为：" + redisTemplate.opsForValue().get(key) + "，新设置过期时间为：" + expireTime + "秒";
                    } else {
                        res = "键值为：" + redisTemplate.opsForValue().get(key) + "，过期时间为：" + redisTemplate.getExpire(key) + "秒";
                    }
                }
            } else {
                if (value != null) {
                    if (expireTime != 0) {
                        redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
                        res = "键：" + key + "，键值为：" + value + "，过期时间为：" + expireTime;
                    } else {
                        redisTemplate.opsForValue().set(key, value);
                        res = "键：" + key + "，键值为：" + value;
                    }
                }
            }
        }
        return res;
    }

    @Override
    public void setData(MyCounters myCounter) {
        this.myCounters = myCounter;
    }
    @Override
    public void setRedis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
