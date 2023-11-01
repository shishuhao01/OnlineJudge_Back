package com.shishuhao.OnlineJudge.model.dto.question;

import lombok.Data;

/**
 * 题目配置
 */
@Data
public class JudgeConfig {
    /**
     * 时间限制 ms
     */
    private long timeLimit;

    /**
     * 内存限制 kB
     */
    private Long memoryLimit;

    /**
     * 堆栈限制 kB
     */
    private long stackLimit;

}
