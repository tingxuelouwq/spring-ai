package com.kevin.springai.rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.List;

@SpringBootTest
public class DocumentReaderTest {

    @Test
    public void testReadText(@Value("classpath:rag/terms-of-service.txt") Resource resource) {
        TextReader textReader = new TextReader(resource);
        List<Document> documents = textReader.read();

        for (Document document : documents) {
            System.out.println(document.getText());
        }
    }

    @Test
    public void testReadMd(@Value("classpath:rag/9_横店影视股份有限公司_0.md") Resource resource) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true) // 水平分割线将创建新的文档
                .withIncludeCodeBlock(false)    // 代码将创建新的文档
                .withIncludeBlockquote(false)   // 引用将创建新的文档
                .withAdditionalMetadata("filename", resource.getFilename()) // 指定每个文档添加的元数据
                .build();
        MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
        List<Document> documents = markdownDocumentReader.read();
        for (Document document : documents) {
            System.out.println(document.getText());
            System.out.println(document.getMetadata());
            System.out.println("--------------------------");
        }
    }
}
