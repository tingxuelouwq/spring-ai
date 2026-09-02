package com.kevin.springai.rag;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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
public class DocumentSplitterTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }

    /**
     * 只要Token数合理就行
     * 不要想着严格按照主题来分，因为例如企业级知识库，文档资料是各式各样的
     */
    @Test
    public void testTokenTextSplitter(@Value("classpath:rag/terms-of-service.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        List<Document> documents =  textReader.read();

//        TokenTextSplitter splitter = new TokenTextSplitter(100, 10, 5, 5000, false);
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> apply = splitter.split(documents);

        apply.forEach(System.out::println);
    }

    @Test
    public void testChineseTokenTextSplitter(@Value("classpath:rag/terms-of-service.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.read();

        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter(100, 10, 5, 5000, false);
        List<Document> apply = splitter.apply(documents);

        apply.forEach(System.out::println);
    }

    @Test
    public void testKeywordMetadataEnricher(
            @Autowired DashScopeChatModel chatModel,
            @Autowired VectorStore vectorStore,
            @Value("classpath:rag/terms-of-service.txt") Resource resource) {

        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();

        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter();
        documents = splitter.apply(documents);

        String templateStr = """
                请从以下文档内容中提取 5 个最重要的关键词。
                关键词必须用中文输出。
                请以 JSON 数组格式返回，不要包含其他内容。
                示例输出：["关键词1", "关键词2", "关键词3", "关键词4", "关键词5"]
                
                文档内容：
                {context_str}
                
                关键词 JSON 数组：
                """;

        PromptTemplate finalTemplate = new PromptTemplate(templateStr);

        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(chatModel)
                .keywordsTemplate(finalTemplate)
                .build();

        documents = enricher.apply(documents);

        for (Document document : documents) {
            System.out.println(document.getMetadata());
        }
    }
}
