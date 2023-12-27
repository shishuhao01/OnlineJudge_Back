package com.shishuhao.OnlineJudge.controller;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shishuhao.OnlineJudge.common.BaseResponse;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.common.ResultUtils;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBoxProxy;
import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandboxFactory;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeRequest;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeResponse;
import com.shishuhao.OnlineJudge.model.dto.question.JudgeCase;
import com.shishuhao.OnlineJudge.model.dto.question.JudgeConfig;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.JudgeInfo;
import com.shishuhao.OnlineJudge.model.dto.userCompetitionRecord.UserCompetitionRecordAddRequest;
import com.shishuhao.OnlineJudge.model.dto.userCompetitionRecord.UserCompetitionRecordDoQuestionRequest;
import com.shishuhao.OnlineJudge.model.entity.Competition;
import com.shishuhao.OnlineJudge.model.entity.Question;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.entity.UserCompetitionRecord;
import com.shishuhao.OnlineJudge.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户比赛记录表 前端控制器
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/userCompetitionRecord")
public class UserCompetitionRecordController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private UserCompetitionRecordService userCompetitionRecordService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionSubmitService questionSubmitService;
    @Value("${codesandbox.type}")
    private String type;


    @PostMapping("/get/UserCompetitionRecord")
    public BaseResponse<UserCompetitionRecord> getUserCompetitionRecord(long competitionId, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);

        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户没登录");
        }

        long userId = loginUser.getId();

        Competition competitionServiceById = competitionService.getById(competitionId);
        if (competitionServiceById == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }

        QueryWrapper<UserCompetitionRecord> userCompetitionRecordQueryWrapper =
                userCompetitionRecordService.setCompetitionTimeWrapper(competitionId, userId);

        UserCompetitionRecord userCompetitionRecordServiceOne =
                userCompetitionRecordService.getOne(userCompetitionRecordQueryWrapper);

        if (userCompetitionRecordServiceOne == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户未报名");
        }

        return ResultUtils.success(userCompetitionRecordServiceOne);


    }


    @PostMapping("/create")
    public BaseResponse<Long> addCompetition(@RequestBody UserCompetitionRecordAddRequest userCompetitionRecordAddRequest,
                                             HttpServletRequest httpServletRequest) {
        if (userCompetitionRecordAddRequest == null) {
            throw new RuntimeException("参数为空");
        }

        User loginUser = userService.getLoginUser(httpServletRequest);

        if (loginUser == null) {
            throw new RuntimeException("未登录");
        }
        long userId = loginUser.getId();

        User loginUser2 = userService.getById(userCompetitionRecordAddRequest.getUserId());
        if (loginUser2 == null || !loginUser2.equals(loginUser)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户登录信息出错");
        }


        long competitionId = userCompetitionRecordAddRequest.getCompetitionId();

        Competition competitionServiceById = competitionService.getById(competitionId);

        if (competitionServiceById == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛不存在");
        }

        UserCompetitionRecord userCompetitionRecord = new UserCompetitionRecord();
        userCompetitionRecord.setScore(0);
        userCompetitionRecord.setQuestionAccepted("");
        BeanUtils.copyProperties(userCompetitionRecordAddRequest, userCompetitionRecord);
        userCompetitionRecord.setCompetitionTime(competitionServiceById.getDate());

        boolean save = userCompetitionRecordService.save(userCompetitionRecord);

        if (!save) {
            throw new RuntimeException("系统错误");
        }
        return ResultUtils.success(userId);
    }


    @PostMapping("/application")
    public BaseResponse<Long> applicationCompetition(long competitionId, HttpServletRequest httpServletRequest) {
        //判断用户是否登录
        User loginUser = userService.getLoginUser(httpServletRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "未登录");
        }
        Long userId = loginUser.getId();

        //判断比赛是否存在
        Competition competitionServiceById = competitionService.getById(competitionId);
        if (competitionServiceById == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛不存在");
        }

        //判断用户是否报名比赛
        QueryWrapper<UserCompetitionRecord> userCompetitionRecordQueryWrapper = userCompetitionRecordService.setCompetitionTimeWrapper(competitionId, userId);
        UserCompetitionRecord userCompetitionRecord = userCompetitionRecordService.getOne(userCompetitionRecordQueryWrapper);
        if (userCompetitionRecord == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户未报名比赛");
        }

        //设置用户开始比赛时间
        LocalDateTime currentTime = LocalDateTime.now();
        userCompetitionRecord.setStartTime(currentTime);
        boolean save = userCompetitionRecordService.updateById(userCompetitionRecord);


        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常");
        }
        return ResultUtils.success(userCompetitionRecord.getId());
    }


    @PostMapping("online/competition")
    public BaseResponse<Long> DoCompetitionQuestion(@RequestBody UserCompetitionRecordDoQuestionRequest userCompetitionRecordDoQuestionRequest,
                                                    HttpServletRequest httpServletRequest) {

        //1.判断用户是否登录
        User loginUser = userService.getLoginUser(httpServletRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未报名比赛");
        }

        //2.判断比赛是否存在
        long competitionId = userCompetitionRecordDoQuestionRequest.getCompetitionId();
        Competition competitionServiceById = competitionService.getById(competitionId);
        if (competitionServiceById == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "比赛不存在");
        }

        //判断问题是否存在
        long questionId = userCompetitionRecordDoQuestionRequest.getQuestionId();
        Question questionById = questionService.getById(questionId);
        if (questionById == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有此题目");
        }

        //判断比赛中是否含有这个题目
        String question = competitionServiceById.getQuestion();
        String question1 = question.substring(1, question.length() - 1);
        String questionList = question1.replace(" ", "");
        String[] split = questionList.split(",");
        List<String> strings = Arrays.asList(split);
        int everyScore = 100 / strings.size();
        String questionContains = Long.toString(questionId);
        if (!strings.contains(questionContains)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }

        //判断用户的语言是否合法
        String language = userCompetitionRecordDoQuestionRequest.getLanguage();
        if (!language.equals(competitionServiceById.getLanguageType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        String code = userCompetitionRecordDoQuestionRequest.getCode();
        //查出用户比赛信息表的记录
        QueryWrapper<UserCompetitionRecord> userCompetitionRecordQueryWrapper = userCompetitionRecordService.setCompetitionTimeWrapper(competitionId, loginUser.getId());
        UserCompetitionRecord userCompetitionMsg = userCompetitionRecordService.getOne(userCompetitionRecordQueryWrapper);
        if (userCompetitionMsg == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //用户做第一道题，开始为空，判断用户是否重复做题
        String questionAccepted = userCompetitionMsg.getQuestionAccepted();
        if (questionAccepted == null) {
            questionAccepted = "";
        }
        String[] s = questionAccepted.split(" ");
        List<String> strings1 = Arrays.asList(s);
        if (strings1.contains(questionContains)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "此题已经做过");
        }
        int currentScore = userCompetitionMsg.getScore();


        //获取题目的输入用例，输出用例，还有判题配置信息

        String judgeCase = questionById.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String> outputList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        String judgeConfigStr = questionById.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        long timeMax = judgeConfig.getTimeLimit();
        Long memoryMax = judgeConfig.getMemoryLimit();


        //封装为判题请求类
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(inputList);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage(language);


        //3.调用沙箱，获取到执行结果，
        CodeSandBox codeSandBox = CodeSandboxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);


        //代码沙箱执行的结果
        List<String> outputListResult = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        String message = judgeInfo.getMessage();
        Long memoryNeed = judgeInfo.getMemoryLimit();
        Long timeNeed = judgeInfo.getTime();
        //1.代码是否执行成功
        if (!message.equals("成功")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码执行异常");
        }
        //2.判断时间和内存是否超出题目给的条件。
        if (memoryNeed > memoryMax || timeNeed > timeMax) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码执行结果超出题目限制");
        }
        //判断题目输出实例和给出结果输出实例
        if (outputListResult.size() != outputList.size()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "执行结果错误");
        }
        for (int i = 0; i < outputListResult.size(); i++) {
            if (!outputListResult.get(i).equals(outputList.get(i))) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "执行结果错误");
            }
        }


        //到此处题目正确，修改用户比赛信息
        userCompetitionMsg.setQuestionAccepted(questionAccepted + " " + questionId);
        currentScore += everyScore;
        userCompetitionMsg.setScore(currentScore);
        userCompetitionRecordService.updateById(userCompetitionMsg);

        return ResultUtils.success(questionId);
    }


}

