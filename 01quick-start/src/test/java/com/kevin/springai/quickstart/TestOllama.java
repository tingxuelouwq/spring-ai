package com.kevin.springai.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestOllama {

    @Test
    public void testOllama(@Autowired OllamaChatModel ollamaChatModel) {
        String content = ollamaChatModel.call("你好，你是谁");
        System.out.println(content);
    }

    @Test
    public void testOllamaStream(@Autowired OllamaChatModel ollamaChatModel) {
        Flux<String> stream = ollamaChatModel.stream("你好，你是谁？");
        stream.toIterable().forEach(System.out::println);
    }

    @Test
    public void testMultimodality(@Autowired OllamaChatModel ollamaChatModel) {
        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("gemma3:4b")
                .build();

        ClassPathResource imageResource = new ClassPathResource("ea9de805-4452-4603-bc6f-41d04109d6da2449891665.png");
        Media media = new Media(MimeTypeUtils.IMAGE_PNG, imageResource);

        ChatResponse response = ollamaChatModel.call(
                new Prompt(
                        UserMessage.builder().media(media)
                                .text("识别图片").build(),
                        ollamaOptions
                )
        );

        System.out.println(response.getResult().getOutput().getText());
    }
}
