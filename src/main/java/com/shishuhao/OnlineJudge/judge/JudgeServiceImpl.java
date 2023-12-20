package com.shishuhao.OnlineJudge.judge;

import cn.hutool.json.JSONUtil;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBoxProxy;
import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandboxFactory;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeRequest;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeResponse;
import com.shishuhao.OnlineJudge.judge.strategy.JudgeContext;
import com.shishuhao.OnlineJudge.model.dto.question.JudgeCase;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.JudgeInfo;
import com.shishuhao.OnlineJudge.model.entity.Question;
import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;
import com.shishuhao.OnlineJudge.model.enums.QuestionSubmitStatusEnum;
import com.shishuhao.OnlineJudge.service.QuestionService;
import com.shishuhao.OnlineJudge.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1.传入题目的提交Id,获取到相应的题目，提交信息（包含代码，编程语言等）,
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据");
        }
        long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据");
        }

        // 2. 更改题目状态 （如果题目的提交状态不为等待中，就不用重复执行了）
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        Integer submitNum = question.getSubmitNum();
        Integer acceptedNum = question.getAcceptedNum();
        acceptedNum += 1;
        submitNum += 1;
        question.setSubmitNum(submitNum);
        boolean b1 = questionService.updateById(question);
        if (!b1) {
            System.out.println("题目提交数目修改失败");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统数据库异常");
        }


        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());

        //获得编程语言代码等信息
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCase = question.getJudgeCase();

        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);

        //题目的输入实例
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        boolean b = questionSubmitService.updateById(questionSubmitUpdate);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }

        //3.调用沙箱，获取到执行结果，
        CodeSandBox codeSandBox = CodeSandboxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);

        ExecuteCodeRequest executeCodeRequest =
                ExecuteCodeRequest.builder().code(code).language(language).inputList(inputList).build();
        //执行代码沙箱
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        JudgeInfo judgeInfoRun = executeCodeResponse.getJudgeInfo();
        if (judgeInfoRun.getMessage().equals("编译错误") || judgeInfoRun.getMessage().equals("危险操作")
                || judgeInfoRun.getMessage().equals("超时") || judgeInfoRun.getMessage().equals("程序运行时异常")) {
            //修改数据库的结果
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            //设置判题状态，这个时候判题完毕
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoRun));
            QuestionSubmit questionSubmit1 = questionSubmitService.getById(questionSubmitId);
            b = questionSubmitService.updateById(questionSubmitUpdate);
            if (!b) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
            }
            return questionSubmit1;
        }

        List<String> outputList = executeCodeResponse.getOutputList();
        //判题后给到的信息
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setOutputList(outputList);
        judgeContext.setInputList(inputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo1 = judgeManager.doJudge(judgeContext);

        //修改数据库的结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        //设置判题状态，这个时候判题完毕
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo1));
        if (judgeInfo1.getMessage().equals("成功")) {
            question.setAcceptedNum(acceptedNum);
            boolean isSave = questionService.updateById(question);
            if (!isSave) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统数据库修改通过数目失败");
            }
        }
        b = questionSubmitService.updateById(questionSubmitUpdate);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }
        QuestionSubmit questionSubmit1 = questionSubmitService.getById(questionSubmitId);
        return questionSubmit1;
    }
}
