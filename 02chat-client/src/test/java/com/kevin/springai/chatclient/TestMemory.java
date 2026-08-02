package com.kevin.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMemory {

    @Test
    public void testMemory(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();

        String chatHits = "我叫凯文";
        String content = chatClient.prompt()
                .user(chatHits)
                .call()
                .content();
        System.out.println(content);

        System.out.println("---------------------------------------------");

        chatHits += content;
        chatHits += "我叫什么？";
        content = chatClient.prompt()
                .user(chatHits)
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testChatMemory(@Autowired ChatClient.Builder chatClientBuilder) {
        // 第一轮对话
        // 1. 构建 conversationId（用户隔离）
        String userId = "kevin";
        String conversationId = "user:" + userId;

        // 2. ChatMemory 自动处理：
        //    - 存储用户消息
        //    - 获取历史记录
        //    - 隔离不同用户
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        UserMessage userMessage_1 = new UserMessage("我叫凯文");
        chatMemory.add(conversationId, userMessage_1);

        // 3. 构建 ChatClient，自动加载历史
        ChatClient chatClient = chatClientBuilder.build();
        String response_1 = chatClient.prompt()
                .messages(chatMemory.get(conversationId))  // 自动获取该用户历史
                .call()
                .content();

        // 4. 存储 AI 回复
        chatMemory.add(conversationId, new AssistantMessage(response_1));

        // 第二轮对话
        UserMessage userMessage_2 = new UserMessage("我叫什么？");
        chatMemory.add(conversationId, userMessage_2);

        String response_2 = chatClient.prompt()
                .messages(chatMemory.get(conversationId))
                .call()
                .content();

        chatMemory.add(conversationId, new AssistantMessage(response_2));

        System.out.println(response_2);
    }
}
