package com.kevin.springai.chatclient;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestRedisMemory {

    @Value("${spring.ai.chat.memory.redis.host}")
    private String host;

    @Value("${spring.ai.chat.memory.redis.port}")
    private int port;

    @Value("${spring.ai.chat.memory.redis.password}")
    private String password;

    @Value("${spring.ai.chat.memory.redis.timeout}")
    private int timeout;

    @Test
    public void testRedisMemory(@Autowired ChatClient.Builder chatClientBuilder) {
        RedisChatMemoryRepository redisChatMemoryRepository = RedisChatMemoryRepository.builder()
                .host(host)
                .port(port)
                .password(password)
                .timeout(timeout)
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(20)
                .build();

        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(chatMemory)
                                .conversationId("kevinq")
                                .build())
                .build();

        String content = chatClient.prompt()
                .user("我叫凯文Q")
                .call()
                .content();
        System.out.println(content);
        System.out.println("------------------------------");

        content = chatClient.prompt()
                .user("我叫什么？")
                .call()
                .content();
        System.out.println(content);
    }
}
