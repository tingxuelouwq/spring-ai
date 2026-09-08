package com.kevin.springai.agent.orchestrator_worker;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SimpleOrchestratorWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleOrchestratorWorkerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(DashScopeChatModel dashScopeChatModel) {
		var chatClient =  ChatClient.create(dashScopeChatModel);
		return args -> {
			new SimpleOrchestratorWorkers(chatClient)
					.process("设计一个企业级的员工考勤系统，支持多种打卡方式和报表生成");
		};
	}

}
