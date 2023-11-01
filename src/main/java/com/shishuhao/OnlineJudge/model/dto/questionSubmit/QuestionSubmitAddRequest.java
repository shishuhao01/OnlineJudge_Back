package com.shishuhao.OnlineJudge.model.dto.questionSubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目创建请求
 *
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */

    private Long questionId;



}