package com.kevin.springai.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestDeepSeek {

    @Test
    public void testDeepSeek(@Autowired DeepSeekChatModel deepSeekChatModel) {
        String content = deepSeekChatModel.call("你好，你是谁");
        System.out.println(content);
    }

    @Test
    public void testDeepSeekStream(@Autowired DeepSeekChatModel deepSeekChatModel) {
        Flux<String> stream = deepSeekChatModel.stream("你好，你是谁");
        stream.toIterable().forEach(System.out::print);
    }

    @Test
    public void testChatOptions(@Autowired DeepSeekChatModel deepSeekChatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-chat")
                .temperature(1.9d)
                .build();
        Prompt prompt = new Prompt("请写一句诗描述清晨。", options);
        ChatResponse chatResponse = deepSeekChatModel.call(prompt);
        System.out.println(chatResponse.getResult().getOutput().getText());
    }
}
