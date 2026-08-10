package com.kevin.springai.multiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
public class AiController {

    @Autowired
    private ChatClient planningChatClient;

    @Autowired
    private ChatClient botChatClient;

    @GetMapping(value = "/stream", produces = "text/stream;charset=UTF-8")
    public Flux<String> stream(@RequestParam String message) {
        // 创建一个可以多次发射数据的管道，Sinks.many()
        // 单播Sink，即该管道只能被一个订阅者订阅，unicast()
        // 缓冲策略，在sink.asFlux()被订阅之前调用的sink.tryEmitNext()会被暂存
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        // 推送消息，数据被缓存
        sink.tryEmitNext("正在计划任务...<br/>");

        new Thread(() -> {
            AiJob.Job job = planningChatClient
                    .prompt()
                    .user(message)
                    .call()
                    .entity(AiJob.Job.class);

            switch (job.jobType()){
                case CANCEL -> {
                    System.out.println(job);
                    if (job.keyInfos().isEmpty()) {
                        sink.tryEmitNext("请输入姓名和订单号");
                    } else {
                        // 模拟执行退票业务
                        sink.tryEmitNext("退票成功!");
                    }
                }
                case QUERY -> {
                    System.out.println(job);
                    if(job.keyInfos().isEmpty()){
                        sink.tryEmitNext("请输入订单号");
                    } else {
                        // 模拟执行查询业务
                        sink.tryEmitNext("查询预定信息：xxxx");
                    }
                }
                case OTHER -> {
                    Flux<String> content = botChatClient
                            .prompt()
                            .user(message)
                            .stream()
                            .content();
                    content.doOnNext(sink::tryEmitNext)
                            .doOnComplete(sink::tryEmitComplete)
                            .subscribe();
                }
                default -> {
                    System.out.println(job);
                    sink.tryEmitNext("解析失败");
                }
            }
        }).start();

        // 返回Flux
        return sink.asFlux();
    }
}
