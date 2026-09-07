package com.kevin.springai.rag.observability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TranslationController {

    private final ChatClient chatClient;

    public TranslationController(@Autowired ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/translate")
    public TranslationResponse translate(@RequestBody TranslationRequest request) {
        log.info("Spring AI翻译请求: {} -> {}", request.getSourceLanguage(), request.getTargetLanguage());

        String prompt= String.format(
                "作为专业翻译助手，请将以下%s文本翻译成%s，保持原文的语气和风格：\n%s",
                request.getSourceLanguage(),
                request.getTargetLanguage(),
                request.getText()
        );

        String translatedText= chatClient.prompt()
                .user(prompt)
                .advisors(SimpleLoggerAdvisor.builder().build())
                .call()
                .content();

        return TranslationResponse.builder()
                .originalText(request.getText())
                .translatedText(translatedText)
                .sourceLanguage(request.getSourceLanguage())
                .targetLanguage(request.getTargetLanguage())
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
