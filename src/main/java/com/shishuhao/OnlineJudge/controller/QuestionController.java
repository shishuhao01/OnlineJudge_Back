package com.shishuhao.OnlineJudge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.shishuhao.OnlineJudge.annotation.AuthCheck;
import com.shishuhao.OnlineJudge.common.BaseResponse;
import com.shishuhao.OnlineJudge.common.DeleteRequest;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.common.ResultUtils;
import com.shishuhao.OnlineJudge.constant.UserConstant;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.exception.ThrowUtils;
import com.shishuhao.OnlineJudge.model.dto.question.*;
import com.shishuhao.OnlineJudge.model.entity.Question;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.vo.QuestionVO;
import com.shishuhao.OnlineJudge.service.QuestionService;
import com.shishuhao.OnlineJudge.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/Question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    private final static ObjectMapper objectMapper = new ObjectMapper();

    // region 增删改查

    /**
     * 添加题目
     *
     * @param
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) throws Exception {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }

        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        String config = objectMapper.writeValueAsString(judgeConfig);
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        String cases = GSON.toJson(judgeCase);

        if (config != null && config.length() > 0) {
            question.setJudgeConfig(config);
        }
        if (cases != null && cases.length() > 0) {
            question.setJudgeCase(cases);
        }
        //对前端的题目进行校验
        questionService.validQuestion(question, true);

        //获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        //设置点赞数
        question.setFavourNum(0);
        question.setThumbNum(0);

        //是否添加成功
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) throws Exception {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //id:问题id
        long id = deleteRequest.getId();

        User loginUser = userService.getLoginUser(request);

        if (loginUser == null) {
            throw new Exception(String.valueOf(ErrorCode.NOT_FOUND_ERROR));
        }
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);

        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员和题目创建者可删除
        if (!loginUser.getId().equals(oldQuestion.getUserId())  && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 对所有题目都可更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request) throws JsonProcessingException {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断传进来对应的id题目是否是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);

        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null && judgeCase.size() > 0) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(objectMapper.writeValueAsString(judgeConfig));
        }

        // 参数校验
        questionService.validQuestion(question, false);

        // 仅管理员可以对所有的题目进行修改
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id , HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (!question.getUserId().equals(loginUser.getId()) && userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return ResultUtils.success(question);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {

        long pageNum= questionQueryRequest.getPageNum();
        long pageSize = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> page = new Page(pageNum,pageSize);
        QueryWrapper<Question> queryWrapper =  questionService.getQueryWrapper(questionQueryRequest);
        questionService.page(page,queryWrapper);
        //用户查询信息中不包含judgeCase这种类
        return ResultUtils.success(questionService.getQuestionVOPage(page, request));
    }

    /**
     * 管理员获取所有题目信息
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Question>> getQuestionList (@RequestBody QuestionQueryRequest questionQueryRequest,HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断是否是管理员，不是管理员不能获得某些信息
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        long pageNum = questionQueryRequest.getPageNum();
        long pageSize = questionQueryRequest.getPageSize();
        Page<Question> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
        questionService.page(page,queryWrapper);

        return ResultUtils.success(page);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录的用户
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());

        long pageNum = questionQueryRequest.getPageNum();
        long pageSize = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);

        questionService.page(page,queryWrapper);

        return ResultUtils.success(questionService.getQuestionVOPage(page, request));
    }

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) throws JsonProcessingException {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Question question = new Question();
        long id = questionEditRequest.getId();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null && judgeCase.size() > 0) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(objectMapper.writeValueAsString(judgeConfig));
        }

        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);


        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    // todo 用户自己创建的题目用户自己查询也需看到输入用例



}
