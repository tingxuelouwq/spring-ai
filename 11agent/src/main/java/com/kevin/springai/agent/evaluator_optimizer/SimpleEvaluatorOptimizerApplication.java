package com.kevin.springai.agent.evaluator_optimizer;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SimpleEvaluatorOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleEvaluatorOptimizerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(DashScopeChatModel dashScopeChatModel) {
        var chatClient = ChatClient.create(dashScopeChatModel);
        return args -> {
            new SimpleEvaluatorOptimizer(chatClient).loop("""
                    <user input>
                     面试被问： 怎么高效的将100行list<User>数据，转化成map<id，user>，不是用stream。
                    </user input>
                    """);

        };
    }
}
