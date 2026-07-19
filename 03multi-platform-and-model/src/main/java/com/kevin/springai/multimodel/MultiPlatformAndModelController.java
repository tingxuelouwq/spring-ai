package com.kevin.springai.multimodel;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MultiPlatformAndModelController {

    private Map<String, ChatModel> platforms = new HashMap<>();

    public MultiPlatformAndModelController(
            DashScopeChatModel dashScopeChatModel,
            DeepSeekChatModel deepSeekChatModel,
            OllamaChatModel ollamaChatModel
    ) {
        platforms.put("dashscope", dashScopeChatModel);
        platforms.put("deepseek", deepSeekChatModel);
        platforms.put("ollama", ollamaChatModel);
    }

    @GetMapping(value = "/chat", produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(@RequestParam String message,
                             MultiPlatformAndModelOptions options) {
        String platform = options.getPlatform();
        ChatModel chatModel = platforms.get(platform);

        ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel);
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                ChatOptions.builder()
                        .model(options.getModel())
                        .temperature(options.getTemperature())
                        .build()
                )
                .build();

        Flux<String> content = chatClient.prompt().user(message).stream().content();
        return content;
    }
}
