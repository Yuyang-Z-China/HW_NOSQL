package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SetResolver implements TypeResolver {

    private MyCounters myCounters;
    @Autowired
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
