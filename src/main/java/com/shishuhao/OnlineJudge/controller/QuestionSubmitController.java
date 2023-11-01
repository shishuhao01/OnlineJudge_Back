package com.shishuhao.OnlineJudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishuhao.OnlineJudge.common.BaseResponse;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.common.ResultUtils;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.exception.ThrowUtils;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.vo.QuestionSubmitVO;
import com.shishuhao.OnlineJudge.service.QuestionSubmitService;
import com.shishuhao.OnlineJudge.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口 返回前端的最好是将string类型封装成对象，再转为JSON返回给前端.
 * 存在数据库里面的最好是string类型,
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return result 返回提交用户Id
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交题目
        final User loginUser = userService.getLoginUser(request);
        Long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }


    /**
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return 分页查询题目提交记录封装类
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                     HttpServletRequest request) {
        long pageNum= questionSubmitQueryRequest.getCurrent();
        long pageSize = questionSubmitQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);

        //分页查询
        Page<QuestionSubmit> page = new Page(pageNum,pageSize);
        QueryWrapper<QuestionSubmit> queryWrapper =  questionSubmitService.getQueryWrapper(questionSubmitQueryRequest);
        questionSubmitService.page(page,queryWrapper);

        //从request中获取用户信息
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(page,loginUser));
    }

}
