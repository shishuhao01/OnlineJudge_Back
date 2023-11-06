package com.shishuhao.OnlineJudge.judge.strategy;

import com.shishuhao.OnlineJudge.model.dto.question.JudgeCase;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.JudgeInfo;
import com.shishuhao.OnlineJudge.model.entity.Question;
import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 用于定义在策略中的参数
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> outputList;

    private List<String> inputList;

    private Question question;

    private List<JudgeCase> judgeCaseList;

    private QuestionSubmit questionSubmit;

}
