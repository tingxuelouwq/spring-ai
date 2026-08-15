package com.kevin.springai.tools;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToolsController {

    private final ChatClient chatClient;

    public ToolsController(ChatClient.Builder chatClientBuilder,
                           ToolService toolService) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        # 角色
                        你是智能航空客服助手
                        # 要求
                        严禁随意补全或猜测工具调用参数。参数如缺失或语义不准，请不要补充或随意传递，请直接放弃本次工具调用。
                        """)
                .defaultTools(toolService)  // 告诉大模型提供了什么工具，需要什么参数
                .build();
    }

    @GetMapping("/tool")
    public String tool(@RequestParam String message) {
        return chatClient.prompt()
                .options(DashScopeChatOptions.builder()
                        .withTemperature(1.9)
                        .build())
                .user(message)
                .call()
                .content();
    }
}
