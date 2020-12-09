package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ZsetResolver implements TypeResolver {

    private MyCounters myCounters;
    RedisTemplate redisTemplate;

    @Override
    public String resolve() {
        return null;
    }

    @Override
    public void setData(MyCounters myCounters) {
        this.myCounters = myCounters;
    }

    @Override
    public void setRedis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
