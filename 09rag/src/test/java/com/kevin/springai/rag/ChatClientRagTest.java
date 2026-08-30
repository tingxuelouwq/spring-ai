package com.kevin.springai.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootTest
public class ChatClientRagTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }

    @BeforeEach
    public void init(@Autowired VectorStore vectorStore) {
        Document doc1 = Document.builder()
                .text("""
                        预订航班:
                        - 通过我们的网站或移动应用程序预订。
                        - 预订时需要全额付款。
                        - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。
                        """)
                .build();

        Document doc2 = Document.builder()
                .text("""
                        取消预订:
                        - 最晚在航班起飞前 48 小时取消。
                        - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
                        - 退款将在 7 个工作日内处理。
                        """)
                .build();

        List<Document> documents = List.of(doc1, doc2);

        // 存储向量（内部会自动向量化)
        vectorStore.add(documents);
    }

    @Test
    public void testRag(@Autowired ChatClient.Builder chatClientBuilder,
                        @Autowired VectorStore vectorStore) {
        ChatClient chatClient = chatClientBuilder.build();

        String content = chatClient.prompt()
                .user("退票需要多少钱?")
                .advisors(
                        SimpleLoggerAdvisor.builder().build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(5)
                                .similarityThreshold(0.4)
                                .build())
                        .build())
                .call()
                .content();

        System.out.println(content);
    }
}
