package com.kevin.springai.mcp.sse.server;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpSSEApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpSSEApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider userTools(UserToolService userToolService) {
        return MethodToolCallbackProvider.builder().toolObjects(userToolService).build();
    }
}
