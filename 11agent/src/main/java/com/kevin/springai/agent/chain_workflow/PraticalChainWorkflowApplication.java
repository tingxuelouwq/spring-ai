package com.kevin.springai.agent.chain_workflow;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PraticalChainWorkflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(PraticalChainWorkflowApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(DashScopeChatModel dashScopeChatModel) {
		var chatClient =  ChatClient.create(dashScopeChatModel);
		return args -> {
			String requirements = """  
					电商平台需要升级订单处理系统，要求：
					 1. 处理能力提升到每秒1000单
					 2. 支持多种支付方式和优惠券
					 3. 实时库存管理和预警
					 4. 订单状态实时跟踪
					 5. 数据分析和报表功能
					 现有系统：Spring Boot + MySQL，日订单量10万 
					       """;
			  new PracticalChainWorkflow(chatClient).process(requirements);
		};
	}
}
