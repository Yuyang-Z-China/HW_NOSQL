package com.gp.gateway.factory;

import com.gp.gateway.bean.MyCounters;
import com.gp.gateway.utils.DateSplitUtils;
import com.gp.gateway.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FreqResolver implements TypeResolver {

    private MyCounters myCounters;
    RedisTemplate redisTemplate;

    @Override
    public String resolve() throws ParseException {
        String res = "没有操作";
        String key = myCounters.getKeyFields();
        String field = myCounters.getFields();
        String value = myCounters.getValueFields();
        // 有keyFields字段时
        if (key != null) {
            if (redisTemplate.hasKey(key)) {
                if (field != null) {
                    String[] t = DateUtil.StringFormat(field);
                    if (t.length == 1) {
                        if (redisTemplate.opsForHash().hasKey(key, t[0])) {
                            if (value != null) {
                                long val = Long.parseLong(value);
                                redisTemplate.opsForHash().increment(key, t[0], val);
                                res = "键:" + key + "，时段：" + t[0] + "，变化了" + val + "，现在为：" + redisTemplate.opsForHash().get(key, t[0]);
                            } else {
                                res = "键:" + key + "，时段：" + t[0] + "值为：" + redisTemplate.opsForHash().get(key, t[0]);
                            }
                        } else {
                            if (value != null) {
                                redisTemplate.opsForHash().put(key, t[0], value);
                                res = "设置键" + key + "，时段：" + t[0] + "，值为：" + value;
                            } else {
                                res = "没有找到当前时段数据";
                            }
                        }
                    } else if (t.length == 2) {
                        String startStr = t[0];
                        String endStr = t[1];
                        SimpleDateFormat strToDate = new SimpleDateFormat("yyyyMMddHHmm");
                        Date start = strToDate.parse(startStr);
                        Date end = strToDate.parse(endStr);
                        List<DateSplitUtils.DateSplit> dateSplits = DateSplitUtils.splitDate(start, end, DateSplitUtils.IntervalType.HOUR, 1);
                        List<String> timeKeys = new ArrayList<>();
                        for (int i = 0; i < dateSplits.toArray().length; i++) {
                            timeKeys.add(dateSplits.get(i).getStartDateTimeStr());
                        }
                        long total = 0;
                        for (int i = 0; i < timeKeys.size(); i++) {
                            if (redisTemplate.opsForHash().hasKey(key, timeKeys.get(i))) {
                                total += Long.parseLong(redisTemplate.opsForHash().get(key, timeKeys.get(i)).toString());
                            }
                        }
                        res = "键:" + key + "，时段" + startStr + "-->" + endStr + "，总和为：" + total;
                    }
                } else {
                    Cursor<Map.Entry<Object, Object>> scan = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().build());
                    while (scan.hasNext()) {
                        Map.Entry<Object, Object> next = scan.next();
                        System.out.println("field:" + next.getKey() + "，value:" + next.getValue());
                    }

                    res = "以上为该key中所有field和field的值";
                }
            } else {
                if (field != null) {
                    String[] t = DateUtil.StringFormat(field);
                    if (t.length == 1) {
                        if (value != null) {
                            redisTemplate.opsForHash().put(key, t[0], value);
                            res = "键:" + key + "，时段：" + t[0] + "，设置值为：" + value;
                        }
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
