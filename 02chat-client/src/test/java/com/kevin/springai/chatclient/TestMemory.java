package com.kevin.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

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

    @Test
    public void testAdvisor(@Autowired ChatClient.Builder chatClientBuilder,
                            @Autowired ChatMemory chatMemory) {
        String userId = "kevin";
        String conversationId = "user:" + userId;

        ChatClient chatClient = chatClientBuilder.defaultAdvisors(
                PromptChatMemoryAdvisor.builder(chatMemory).conversationId(conversationId).build())
                .build();

        String content = chatClient.prompt()
                .user("我叫凯文")
                .call()
                .content();
        System.out.println(content);
        System.out.println("---------------------------");

        content = chatClient.prompt()
                .user("我叫什么？")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testMultiUserIsolation(@Autowired ChatClient.Builder chatClientBuilder,
                                       @Autowired ChatMemory chatMemory) {
        ChatClient chatClient = chatClientBuilder.defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        // 用户A 对话
        String userA = "user-A";
        String responseA1 = chatClient.prompt()
                .user("我叫张三，今年25岁")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userA))
                .call()
                .content();
        System.out.println("用户A 第一次: " + responseA1);

        String responseA2 = chatClient.prompt()
                .user("我叫什么？几岁？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userA))
                .call()
                .content();
        System.out.println("用户A 第二次: " + responseA2);

        System.out.println("--------------------------------------------------------------------------");

        // 用户B 对话（完全隔离）
        String userB = "user-B";
        String responseB1 = chatClient.prompt()
                .user("我叫什么？几岁？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userB))
                .call()
                .content();
        System.out.println("用户B 第一次: " + responseB1);
    }

    @TestConfiguration
    static class Config {
        @Bean
        ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory
                    .builder()
                    .maxMessages(2) // 只保留2条消息 = 1轮对话
                    .chatMemoryRepository(chatMemoryRepository).build();
        }
    }

    @Test
    public void testChatClientWithMax2(@Autowired ChatClient.Builder chatClientBuilder,
                                       @Autowired ChatMemory chatMemory) {
        String userId = "test-user";
        String conversationId = "user:" + userId;

        ChatClient client = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(conversationId)
                                .build()
                )
                .build();

        // 第1轮对话
        System.out.println("=== 第1轮 ===");
        String response1 = client.prompt()
                .user("我叫凯文，今年25岁，是一名Java程序员")
                .call()
                .content();
        System.out.println("AI: " + response1);

        // 第2轮对话（会覆盖第1轮）
        System.out.println("\n=== 第2轮 ===");
        String response2 = client.prompt()
                .user("我喜欢打篮球")
                .call()
                .content();
        System.out.println("AI: " + response2);

        // 第3轮对话：查询个人信息
        System.out.println("\n=== 第3轮 ===");
        String response3 = client.prompt()
                .user("我叫什么？今年几岁？")
                .call()
                .content();
        System.out.println("AI: " + response3);
    }
}
