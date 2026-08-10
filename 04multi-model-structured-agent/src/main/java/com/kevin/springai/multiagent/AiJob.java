package com.kevin.springai.multiagent;

import java.util.Map;

public class AiJob {

    public enum JobType {
        CANCEL,
        QUERY,
        OTHER,
    }

    /**
     *
     * @param jobType   任务类型
     * @param keyInfos  <用户名,预定号>映射表
     */
    record Job(JobType jobType, Map<String, String> keyInfos) {
    }
}
