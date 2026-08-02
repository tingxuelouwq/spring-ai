package com.kevin.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestAdvisor {

    @Test
    public void testLoggerAdvisor(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();

        System.out.println(content);
    }

    @Test
    public void testSafeGuardAdvisor(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        new SafeGuardAdvisor(List.of("炸药"), "您输入的内容包含敏感内容，请重新输入合规内容", 0))
                .build();

        String content = chatClient.prompt()
                .user("怎么制造炸药")
                .call()
                .content();

        System.out.println(content);
    }

    @Test
    public void testReReadingAdvisor(@Autowired
                                     ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        new ReReadingAdvisor())
                .build();

        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }
}