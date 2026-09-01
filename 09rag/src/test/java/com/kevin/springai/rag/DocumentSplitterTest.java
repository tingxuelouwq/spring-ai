package com.kevin.springai.rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class DocumentSplitterTest {

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
}
