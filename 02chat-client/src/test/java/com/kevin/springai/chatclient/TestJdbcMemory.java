package com.kevin.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author 王琪
 * @since 2026/8/6 11:11
 */
@SpringBootTest
public class TestJdbcMemory {

    @Test
    public void testJdbcMemory(@Autowired ChatClient.Builder chatClientBuilder,
                               @Autowired JdbcChatMemoryRepository jdbcChatMemoryRepository){
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(20)
                .build();

        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(chatMemory)
                                .conversationId("kevin")
                                .build())
                .build();

        String content = chatClient.prompt()
                .user("我叫凯文")
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
