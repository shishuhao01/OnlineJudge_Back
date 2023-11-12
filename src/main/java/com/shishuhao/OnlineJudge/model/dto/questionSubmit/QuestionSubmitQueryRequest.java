package com.shishuhao.OnlineJudge.model.dto.questionSubmit;

import com.shishuhao.OnlineJudge.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 题目判题状态
     */
    private Integer status;


    /**
     * 题目 id
     */
    private Long questionId;


    /**
     * 用户id
     */
    private Long userId;



}