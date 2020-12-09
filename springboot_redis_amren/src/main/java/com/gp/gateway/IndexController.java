package com.gp.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp.gateway.bean.*;
import com.gp.gateway.factory.NumResolver;
import com.gp.gateway.factory.TypeFactory;
import com.gp.gateway.vo.ResultBody;
import com.sun.tracing.dtrace.ModuleAttributes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * amren
 */
@Controller
@RequestMapping("")
@CrossOrigin
@Slf4j
public class IndexController {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    TypeFactory typeFactory;
    @Autowired
    NumResolver numResolver;

    // counter映射
    private static ListMyActions actionsMap = new ListMyActions();
    private static ListMyCounters counterMap = new ListMyCounters();

    @RequestMapping("/")
    public ModelAndView page() {
        reloadConfigJson();
        ModelAndView modelAndView = new ModelAndView();
        String all = showAllActions();
        modelAndView.setViewName("index");
        modelAndView.addObject("data",all.split("#"));
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping("/do/{name}")
    public ResultBody index(@PathVariable String name) {
        reloadConfigJson();
        log.info("收到操作" + name);
        // 去actionMap中查询是否存在
        List<MyActions> myActionsCollect = actionsMap.getActions().stream().filter(s -> name.equals(s.getName())).collect(Collectors.toList());
        if (myActionsCollect.size() > 0) {
            // 将指定action中Counter取出
            MyActions myActions = myActionsCollect.get(0);
            List<String> counterList = myActions.getRetrieve().stream().map(Retrieve::getCounterName).collect(Collectors.toList());
            List<MyCounters> counters = counterMap.getCounters().stream().filter(s -> counterList.contains(s.getCounterName())).collect(Collectors.toList());
            // 执行counters
            ResultBody resultBody = resolveCounters(counters);
            return resultBody;
        } else {
            ResultBody b = ResultBody.error("您输入的action不存在");
            return b;
        }
    }

    private String showAllActions() {
        StringBuilder stringBuilder = new StringBuilder();
        log.info("您配置中的actions如下：");
        actionsMap.getActions().forEach((name) -> stringBuilder.append(name.getName() + "#"));
        return stringBuilder.toString();
    }

    public ResultBody resolveCounters(List<MyCounters> counterList) {
        StringBuilder stringBuilder = new StringBuilder();
        counterList.forEach(counter -> {
            log.info(counter.getCounterName() + "解析...");
            String res = null;
            try {
                res = typeFactory.getResolver(counter.getType(), counter, redisTemplate).resolve();
                stringBuilder.append(res + "\n");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            log.info(res);
        });
        return ResultBody.success(stringBuilder.toString());
    }

    public void reloadConfigJson() {
        try {
            log.info("加载Json配置");
            // 清空
            counterMap.setCounters(null);
            actionsMap.setActions(null);
            String actionsPath = "src/main/resources/actions.json";
            String countersPath = "src/main/resources/counters.json";
            //获取配置文件，并转为String
            InputStream inputStream = new FileInputStream(actionsPath);
            String actionsText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream = new FileInputStream(countersPath);
            String countersText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            actionsMap = objectMapper.readValue(actionsText, ListMyActions.class);
            counterMap = objectMapper.readValue(countersText, ListMyCounters.class);
            log.info("读取完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
