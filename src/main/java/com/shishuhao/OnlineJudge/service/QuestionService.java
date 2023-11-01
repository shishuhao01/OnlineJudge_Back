package com.shishuhao.OnlineJudge.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shishuhao.OnlineJudge.model.dto.question.QuestionQueryRequest;
import com.shishuhao.OnlineJudge.model.entity.Question;
import com.shishuhao.OnlineJudge.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务
 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param QuestionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest QuestionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param QuestionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> QuestionPage, HttpServletRequest request);
}
