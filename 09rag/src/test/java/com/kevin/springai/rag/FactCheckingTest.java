package com.kevin.springai.rag;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class FactCheckingTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }

    @Test
    public void testFactChecking(@Autowired DashScopeChatModel chatModel) {

        // 创建 FactCheckingEvaluator，接收一个LLM参数
        var factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(chatModel));

        // 发送给大模型的上下文
        Document doc = Document.builder()
                .text("""
                        取消预订:
                        - 最晚在航班起飞前 48 小时取消。
                        - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
                        - 退款将在 7 个工作日内处理。
                        """)
                .build();
        List<Document> documents = List.of(doc);

        // AI回答
        String response = "经济舱取消费用75 美元";

        // 创建 EvaluationRequest，接收三个参数，分别表示用户问题（用户的实际查询）、上下文（从向量数据库中检索到的相关文档）、响应（AI模型生成的答案）
        EvaluationRequest evaluationRequest = new EvaluationRequest(documents, response);

        // 执行评估
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        System.out.println(evaluationResponse);
    }

    @Test
    public void testRelevancyEvaluator(@Autowired DashScopeChatModel chatModel) {
        // 创建 RelevancyEvaluator
        var evaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        // 上下文和声明
        String context = "地球是距离太阳的第三颗行星，也是已知唯一孕育生命的天文物体。";
        String claim = "地球是距离太阳的第四颗行星，也是已知唯一孕育生命的天文物体。";

        // 创建 EvaluationRequest
        EvaluationRequest evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);

        // 执行评估
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        System.out.println(evaluationResponse);
    }

    @Test
    public void testRelevancyEvaluator2(@Autowired DashScopeChatModel chatModel) {
        // 创建 RelevancyEvaluator，接收一个 LLM 参数
        RelevancyEvaluator evaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        // 用户问题
        String userQuery = "经济舱的取消费用是多少？";

        // 发送给大模型的上下文
        Document doc = Document.builder()
                .text("""
                    取消预订:
                    - 最晚在航班起飞前 48 小时取消。
                    - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
                    - 退款将在 7 个工作日内处理。
                    """)
                .build();
        List<Document> documents = List.of(doc);

        // AI 回答（可能相关或不相关）
        String response = "经济舱取消费用75 美元";  // ✅ 相关

        // 或测试不相关场景
//         String response = "今天天气很好";  // ❌ 不相关

        // 创建 EvaluationRequest（用户问题、上下文、响应）
        EvaluationRequest request = new EvaluationRequest(userQuery, documents, response);

        // 执行评估
        EvaluationResponse result = evaluator.evaluate(request);

        System.out.println(result);
    }


    @Test
    public void testRelevancyEvaluator3(@Autowired VectorStore vectorStore,
                                        @Autowired DashScopeChatModel dashScopeChatModel) {
        List<Document> documents = List.of(
                new Document("""
                        1. 预订航班
                        - 通过我们的网站或移动应用程序预订。
                        - 预订时需要全额付款。
                        - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。
                        """),
                new Document("""
                        2. 更改预订
                        - 允许在航班起飞前 24 小时更改。
                        - 通过在线更改或联系我们的支持人员。
                        - 改签费：经济舱 50，豪华经济舱 30，商务舱免费。
                        """),
                new Document("""
                        3. 取消预订
                        - 最晚在航班起飞前 48 小时取消。
                        - 取消费用：经济舱 75 美元，豪华经济舱50美元，商务舱25美元。
                        - 退款将在 7 个工作日内处理。
                        """));
        vectorStore.add(documents);

        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        String query = "退票费用";
        ChatResponse chatResponse = ChatClient.builder(dashScopeChatModel)
                .build().prompt(query).advisors(retrievalAugmentationAdvisor).call().chatResponse();

        EvaluationRequest evaluationRequest = new EvaluationRequest(
                // The original user question
                query,
                // The retrieved context from the RAG flow
                chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT),
                // The AI model's response
                chatResponse.getResult().getOutput().getText()
        );

        RelevancyEvaluator evaluator = new RelevancyEvaluator(ChatClient.builder(dashScopeChatModel));

        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        System.out.println(evaluationResponse);
        System.out.println(chatResponse.getResult().getOutput().getText());
    }
}
