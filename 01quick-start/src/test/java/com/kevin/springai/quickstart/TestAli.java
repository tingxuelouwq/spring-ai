package com.kevin.springai.quickstart;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@SpringBootTest
public class TestAli {

    @Test
    public void testQwen(@Autowired DashScopeChatModel dashScopeChatModel) {
        String content = dashScopeChatModel.call("你好，你是谁");
        System.out.println(content);
    }

    @Test
    public void testText2Image(@Autowired DashScopeImageModel dashScopeImageModel) {
        DashScopeImageOptions imageOptions = DashScopeImageOptions.builder()
                .withModel("wanx2.1-t2i-plus")
                .build();

        ImageResponse imageResponse = dashScopeImageModel.call(
                new ImagePrompt("一间有着精致窗户的花店，漂亮的木质门，摆放着花朵", imageOptions));
        String imageUrl = imageResponse.getResult().getOutput().getUrl();

        // 图片URL
        System.out.println(imageUrl);
    }

    @Test
    public void testText2Audio(@Autowired DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel) throws IOException {
        DashScopeSpeechSynthesisOptions options = DashScopeSpeechSynthesisOptions.builder()
                .model("cosyvoice-v1")
                .voice("longwan")
                .build();

        SpeechSynthesisResponse response = dashScopeSpeechSynthesisModel.call(
                new SpeechSynthesisPrompt("请问北京明天天气如何？", options));

        File file = new File("output.mp3");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ByteBuffer byteBuffer = response.getResult().getOutput().getAudio();
            fos.write(byteBuffer.array());
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }
}
