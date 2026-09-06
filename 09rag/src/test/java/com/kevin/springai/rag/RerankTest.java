package com.kevin.springai.rag;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class RerankTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public VectorStore vectorStore(OllamaEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }

    @BeforeEach
    public void init(@Autowired VectorStore vectorStore,
                     @Value("classpath:rag/terms-of-service.txt")Resource resource) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();

        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter(80,10,5,10000,true);
        documents = splitter.apply(documents);

        vectorStore.add(documents);
    }

    @Test
    public void testRerank(@Autowired DashScopeChatModel dashScopeChatModel,
                           @Autowired VectorStore vectorStore,
                           @Autowired DashScopeRerankModel dashScopeRerankModel) {
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel).build();

        RetrievalRerankAdvisor retrievalRerankAdvisor = new RetrievalRerankAdvisor(
                vectorStore, dashScopeRerankModel, SearchRequest.builder().topK(200).build());

        String content = chatClient.prompt()
                .user("退费费用？")
                .advisors(retrievalRerankAdvisor)
                .call()
                .content();

        System.out.println(content);
    }
}
