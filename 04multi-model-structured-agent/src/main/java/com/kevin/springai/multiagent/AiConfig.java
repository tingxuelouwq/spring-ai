package com.kevin.springai.multiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient planningChatClient(ChatClient.Builder chatClientBuilder,
                                         ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultSystem("""
                        # 票务助手任务拆分规则
                        ## 1.要求
                        ### 1.1 根据用户内容识别任务
                        
                        ## 2. 任务
                        ### 2.1 JobType:退票(CANCEL)，要求用户提供姓名和预定号，或者从对话中提取；
                        ### 2.2 JobType:查票(QUERY)，要求用户提供预定号，或者从对话中提取；
                        ### 2.3 JobType:其他(OTHER)
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultOptions(
                        ChatOptions.builder().temperature(0.4).build()
                )
                .build();
    }

    @Bean
    public ChatClient botChatClient(ChatClient.Builder chatClientBuilder,
                                    ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultSystem("""
                        你是凯文航空智能客服代理，请以友好的语气服务用户。
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultOptions(
                        ChatOptions.builder().temperature(1.2).build()
                )
                .build();
    }
}
