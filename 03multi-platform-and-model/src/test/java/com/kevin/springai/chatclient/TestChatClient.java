package com.kevin.springai.chatclient;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestChatClient {

    @Test
    public void testChatClient(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testStreamChatClient(@Autowired ChatClient.Builder chatClientBuidler) {
        ChatClient chatClient = chatClientBuidler.build();
        Flux<String> content = chatClient.prompt()
                .user("你好")
                .stream()
                .content();
        content.toIterable().forEach(System.out::println);
    }

    @Test
    public void testMultiChatClient(@Autowired DashScopeChatModel dashScopeChatModel) {
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel).build();
        String content = chatClient.prompt()
                .user("你好，你是谁？")
                .call()
                .content();
        System.out.println(content);
    }
}
