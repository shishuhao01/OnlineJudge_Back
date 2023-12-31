package com.shishuhao.OnlineJudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.constant.CommonConstant;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.judge.JudgeService;
import com.shishuhao.OnlineJudge.mapper.QuestionSubmitMapper;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.shishuhao.OnlineJudge.model.entity.Question;
import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.enums.QuestionSubmitLanguageEnum;
import com.shishuhao.OnlineJudge.model.enums.QuestionSubmitStatusEnum;
import com.shishuhao.OnlineJudge.model.vo.QuestionSubmitVO;
import com.shishuhao.OnlineJudge.model.vo.QuestionVO;
import com.shishuhao.OnlineJudge.model.vo.UserVO;
import com.shishuhao.OnlineJudge.service.QuestionService;
import com.shishuhao.OnlineJudge.service.QuestionSubmitService;
import com.shishuhao.OnlineJudge.service.UserService;
import com.shishuhao.OnlineJudge.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目提交 服务实现类
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Override
    public boolean save(QuestionSubmit entity) {
        return super.save(entity);
    }

    @Autowired
    @Lazy
    private QuestionSubmitService questionSubmitService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    @Lazy
    private JudgeService judgeService;

    @Autowired
    private UserService userService;

    /**
     * 题目提交
     *
     * @param
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "请登录");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        QuestionSubmit questionSubmit = new QuestionSubmit();
        //设置存储变量QuestionSubmit默认值
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        boolean save = questionSubmitService.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        Long questionSubmitId = questionSubmit.getId();

//        CompletableFuture.runAsync(() -> {
        judgeService.doJudge(questionSubmitId);
//        });

        return questionSubmitId;
    }


    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String language = questionSubmitQueryRequest.getLanguage();
        Long userId = questionSubmitQueryRequest.getUserId();
        Integer status = questionSubmitQueryRequest.getStatus();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.orderByDesc("updateTime");
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }


    public QueryWrapper<QuestionSubmit> getUserQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest, long userId) {

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未获取当前登录用户");
        }
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.orderByDesc("updateTime");
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }


    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionVO = QuestionSubmitVO.objToVo(questionSubmit);
        //如果不是管理员而且题目提交id与当前登录id不一致时，将查询到到的代码置为空
        if (loginUser.getId() != questionVO.getUserId() && !userService.isAdmin(loginUser)) {
            questionVO.setCode(null);
        }

        return questionVO;
    }

    @Override
    //根据questionPage对象返回questionVo分页对象
    public Page<QuestionSubmitVO> getQuestionSubmitVOPageAdmin(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        if (loginUser == null || !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();

        Page<QuestionSubmitVO> questionVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        //如果查询到的用户提交信息为空，则返回
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOPage = new ArrayList<>();

        for (int i = 0; i < questionSubmitList.size(); i++) {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmitList.get(i));

            Long userId = questionSubmitList.get(i).getUserId();
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);

            Long questionId = questionSubmitList.get(i).getQuestionId();
            Question question = questionService.getById(questionId);
            QuestionVO questionVO = QuestionVO.objToVo(question);

            questionSubmitVO.setUserVO(userVO);
            questionSubmitVO.setQuestionVO(questionVO);

            questionSubmitVOPage.add(questionSubmitVO);
        }
        questionVOPage.setRecords(questionSubmitVOPage);
        return questionVOPage;

        //对每个题目的信息与用户信息进行校验。
//        List<QuestionSubmitVO> QuestionSubmitVOList = questionSubmitList.stream().map(
//                questionSubmitItem -> getQuestionSubmitVO(questionSubmitItem, loginUser)
//        ).collect(Collectors.toList());
//        questionVOPage.setRecords(QuestionSubmitVOList);

    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPageUser(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();

        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());

        //如果查询到的用户提交信息为空，则返回
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> list = new ArrayList<>();

        for (int i = 0; i < questionSubmitList.size(); i++) {
            QuestionSubmit questionSubmit = questionSubmitList.get(i);
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);

            Long questionId = questionSubmit.getQuestionId();
            Question question = questionService.getById(questionId);
            QuestionVO questionVO = QuestionVO.objToVo(question);

            Long userId = questionSubmit.getUserId();
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);

            questionSubmitVO.setQuestionVO(questionVO);
            questionSubmitVO.setUserVO(userVO);


            list.add(questionSubmitVO);
        }
        questionSubmitVOPage.setRecords(list);
        return questionSubmitVOPage;
    }


}

