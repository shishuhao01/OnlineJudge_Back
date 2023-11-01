package com.shishuhao.OnlineJudge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.vo.QuestionSubmitVO;

/**
 * 题目提交 服务类
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest QuestionQuerySubmitRequest);

    /**
     * 获取题目提交封装
     *
     * @param
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交封装
     *
     * @param
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> QuestionSubmitPage, User loginUser);


}
