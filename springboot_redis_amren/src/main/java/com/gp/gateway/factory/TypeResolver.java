package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;

public interface TypeResolver {
    String resolve() throws ParseException;
    void setData(MyCounters myCounters);
    void setRedis(RedisTemplate redisTemplate);
}
