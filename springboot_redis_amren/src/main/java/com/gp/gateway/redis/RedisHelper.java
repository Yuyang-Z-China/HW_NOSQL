package com.gp.gateway.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp.gateway.bean.*;
import com.gp.gateway.factory.TypeFactory;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisHelper {

    // counter映射
    private static ListMyActions actionsMap = new ListMyActions();
    private static ListMyCounters counterMap = new ListMyCounters();
    // 控制台
    private static final PrintWriter out = new PrintWriter(System.err, true);
    // 终端输入
    private static final Scanner in = new Scanner(System.in);

    // typeFactory
    private static final TypeFactory typeFactory = new TypeFactory();

    public static void loadConfigJson() {
        try {
            out.println();
            out.println("加载Json配置");
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
            out.println("读取完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // 加载Json配置文件
        loadConfigJson();
        out.println("作者：xxx");
        // 获取用户所选择的操作
        int choice = getChoice();
        while (choice != 0) {
            if (choice == 1) {
                showAllActions();
            } else if (choice == 2) {
                toResolveAction();
            }
            choice = getChoice();
        }
    }

    private static void showAllActions() {
        out.println("您配置中的actions如下：");
        actionsMap.getActions().forEach((name) -> out.println(name.getName()));
    }

    private static void toResolveAction() throws IOException {
        out.print("请输入想要执行的action：");
        out.flush();
        String name = in.nextLine();
        // 去actionMap中查询是否存在
        List<MyActions> myActionsCollect = actionsMap.getActions().stream().filter(s -> name.equals(s.getName())).collect(Collectors.toList());
        if (myActionsCollect.size() > 0) {
            // 将指定action中Counter取出
            MyActions myActions = myActionsCollect.get(0);
            List<String> counterList = myActions.getRetrieve().stream().map(Retrieve::getCounterName).collect(Collectors.toList());
            List<MyCounters> counters = counterMap.getCounters().stream().filter(s -> counterList.contains(s.getCounterName())).collect(Collectors.toList());
            // 执行counters
            resolveCounters(counters);
        } else {
            out.println("您输入的action不存在");
        }
    }

    public static void resolveCounters(List<MyCounters> counterList) {
        counterList.forEach(counter -> {
            System.out.println(counter.getCounterName() + "执行中...");
            String res = null;
            try {
                res = typeFactory.getResolver(counter.getType(), counter,null).resolve();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(res);
        });
        out.println();
    }


    private static int getChoice() throws IOException {
        int input;
        do {
            try {
                out.println();
                out.print("[0]  退出\n"
                        + "[1]  显示所有actions\n"
                        + "[2]  执行action\n"
                        + "choice> ");
                out.flush();

                input = Integer.parseInt(in.nextLine());

                out.println();

                if (0 <= input && 3 >= input) {
                    break;
                } else {
                    out.println("非法的选择:  " + input);
                }
            } catch (NumberFormatException nfe) {
                out.println(nfe);
            }
        } while (true);

        return input;
    }
}
