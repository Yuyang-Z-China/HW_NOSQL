package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class ListResolver implements TypeResolver {

    RedisTemplate redisTemplate;
    private MyCounters myCounters;
    @Override
    public String resolve() {
        String res = "没有操作";
        String key = myCounters.getKeyFields();
        String value = myCounters.getValueFields();
        int expireTime = myCounters.getExpireTime();
        if (key != null) {
            if (redisTemplate.hasKey(key)) {
                if (redisTemplate.type(key).equals("list")) {
                    if (value != null) {
                        if (expireTime != 0) {
                            redisTemplate.opsForList().leftPush(key, value);
                            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                            res = "键：" + key + "中列表添加新值：" + value + "，key的过期时间为" + redisTemplate.getExpire(key);
                        } else {
                            redisTemplate.opsForList().leftPush(key, value);
                            res = "键：" + key + "中列表添加新值：" + value;
                        }
                    } else {
                        if (expireTime != 0) {
                            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                            List list = redisTemplate.opsForList().range(key, 0, -1);
                            res = "键：" + key + "中列表的值如下：";
                            for (int i = 0; i < list.size(); i++) {
                                res += list.get(i) + " ";
                            }
                        }
                    }
                }
            } else {
                if (value != null) {
                    if (expireTime != 0) {
                        redisTemplate.opsForList().leftPush(key, value);
                        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                        res = "键：" + key + "中列表添加新值：" + value + "，key的过期时间为：" + expireTime;
                    } else {
                        redisTemplate.opsForList().leftPush(key,value);
                        res = "键：" + key + "中列表添加新值：" + value;
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
