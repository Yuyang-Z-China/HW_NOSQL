package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TypeFactory {

    private Map<String, TypeResolver> typeResolveMap = new HashMap<>();


    public TypeFactory() {
        typeResolveMap.put("num", new NumResolver());
        typeResolveMap.put("freq", new FreqResolver());
        typeResolveMap.put("str", new StrResolver());
        typeResolveMap.put("list", new ListResolver());
        typeResolveMap.put("set", new SetResolver());
        typeResolveMap.put("zset", new ZsetResolver());
    }

    public TypeResolver getResolver(String type, MyCounters myCounters,RedisTemplate redisTemplate) {
        typeResolveMap.get(type).setData(myCounters);
        typeResolveMap.get(type).setRedis(redisTemplate);
        return typeResolveMap.get(type);
    }

}
