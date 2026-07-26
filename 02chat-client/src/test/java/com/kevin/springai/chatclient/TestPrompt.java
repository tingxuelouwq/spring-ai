package com.kevin.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class TestPrompt {

    @Test
    public void testSystemPrompt(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                    # 角色说明
                    你是一名专业法律顾问AI
                    
                    ## 回复格式
                    1. 问题分析
                    2. 相关依据
                    3. 梳理和建议
                    
                    **特别注意：**
                    - 不承担律师责任
                    - 不生成涉敏、虚假内容
                """)
                .build();

        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();

        System.out.println(content);
    }

    @Test
    public void testSystemPromptTemplate(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                    # 角色说明
                    你是一名专业法律顾问AI
                    
                    ## 回复格式
                    1. 问题分析
                    2. 相关依据
                    3. 梳理和建议
                    
                    **特别注意：**
                    - 不承担律师责任
                    - 不生成涉敏、虚假内容
                    
                    当前服务的用户：
                    姓名：{name}，年龄：{age}，性别：{sex}
                """)
                .build();

        String content = chatClient.prompt()
                .system(p -> p.param("name", "凯文").param("age", "18").param("sex", "男"))
                .user("你好")
                .call()
                .content();

        System.out.println(content);
    }

    @Test
    public void testSystemPromptTemplateFake(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .build();

        String content = chatClient.prompt()
                .user(u -> u.text("""
                    # 角色说明
                    你是一名专业法律顾问AI
                
                    ## 回复格式
                    1. 问题分析
                    2. 相关依据
                    3. 梳理和建议
                
                    **特别注意：**
                    - 不承担律师责任
                    - 不生成涉敏、虚假内容
                
                    回答用户的法律咨询问题
                    {question}
                """).param("question", "被裁的补偿金"))
                .call()
                .content();

        System.out.println(content);
    }

    @Test
    public void testSystemPromptTemplateCustom(@Autowired ChatClient.Builder chatClientBuilder) {
        // 1. 创建自定义渲染器实例
        StTemplateRenderer customRenderer = StTemplateRenderer.builder()
                .startDelimiterToken('<')   // 使用 <作为开始标记
                .endDelimiterToken('>')     // 使用 > 作为结束标记
                .build();

        // 2. 创建 PromptTemplate
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(customRenderer)
                .template("""
                    角色：你是一位<role>专家
                    
                    任务：请介绍<count>个<field>领域的核心概念
                    
                    要求：
                    1. 每个概念用一句话说明
                    2. 按重要程度排序
                    3. 用<language>回答
                    """)
                .build();

        // 3. 准备参数
        Map<String, Object> params = new HashMap<>();
        params.put("role", "人工智能");
        params.put("count", "3");
        params.put("field", "深度学习");
        params.put("language", "中文");

        // 4. 渲染并执行
        String renderedPrompt = promptTemplate.render(params);
        System.out.println("=== 渲染后的提示 ===\n" + renderedPrompt);

        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient.prompt()
                .user(renderedPrompt)
                .call()
                .content();

        System.out.println("\n=== AI回答 ===\n" + content);
    }

    @Test
    public void testSystemPromptTemplateFile(@Autowired ChatClient.Builder chatClientBuilder,
                                             @Value("classpath:/files/prompt.st") Resource systemResource){
        ChatClient chatClient = chatClientBuilder
                .defaultSystem(systemResource)
                .build();

        String content = chatClient.prompt()
                .system(p -> p.param("name", "凯文").param("age", "18").param("sex", "男"))
                .user("你好")
                .call().content();

        System.out.println(content);
    }
}
