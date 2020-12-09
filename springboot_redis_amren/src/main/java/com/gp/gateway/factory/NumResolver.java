package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@Component
public class NumResolver implements TypeResolver {

    private MyCounters myCounters;
    RedisTemplate redisTemplate;

    @Override
    public String resolve() {
        String res = "没有操作";
        String key = myCounters.getKeyFields();
        String value = myCounters.getValueFields();
        int expireTime = myCounters.getExpireTime();
        // 有keyFields字段时
        if (key != null) {
            // key在redis中存在时
            if (redisTemplate.hasKey(key) && redisTemplate.type(key).name().equalsIgnoreCase("string")) {
                // 有valueFields时
                if (value != null) {
                    // 有expireTime时
                    long val = Long.parseLong(value);
                    if (expireTime != 0) {
                        redisTemplate.opsForValue().increment(key, val);
                        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                        res = "键" + key + "变化了" + val + "，距离过期还有" + expireTime + "秒" + "，现在为：" + redisTemplate.opsForValue().get(key);
                        ;
                    } else {    // 没有expireTime时（为0）
                        redisTemplate.opsForValue().increment(key, val);
                        res = "键" + key + "变化了" + val + "，现在为：" + redisTemplate.opsForValue().get(key);
                    }
                } else {    // 没有valueFields时
                    // 有expireTime时
                    if (expireTime != 0) {
                        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                        res = "键值为：" + redisTemplate.opsForValue().get(key) + "，新设置过期时间为" + expireTime + "秒";
                    } else {    // 没有expireTime时（为0）
                        res = "键值为：" + redisTemplate.opsForValue().get(key) + "，过期时间为：" + redisTemplate.getExpire(key) + "秒";
                    }
                }
            } else {    // key在redis中不存在时
                // 有valueFields
                if (value != null) {
                    // 有expireTime时
                    if (expireTime != 0) {
                        redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
                        res = "新增键：" + key + "，键值为：" + value + "，过期时间为：" + expireTime;
                    } else {    // 没有expireTime时(为0)
                        redisTemplate.opsForValue().set(key, value);
                        res = "新增键：" + key + "，键值为：" + value;
                    }
                } else {    // 没有valueFields
                    res = "没有找到需要展示的键";
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
