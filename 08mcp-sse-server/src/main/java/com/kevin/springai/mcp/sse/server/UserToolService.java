package com.kevin.springai.mcp.sse.server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserToolService {

    Map<String, Double> userScore = Map.of(
            "kaiwen", 99.0,
            "zhangsan", 2.0,
            "lisi", 3.0);

    @Tool(description = "获取用户分数")
    public String getScore(String username) {
        if(userScore.containsKey(username)){
            return userScore.get(username).toString();
        }

        return "未检索到当前用户";
    }
}
