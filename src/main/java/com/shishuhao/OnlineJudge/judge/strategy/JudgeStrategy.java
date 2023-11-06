package com.shishuhao.OnlineJudge.judge.strategy;

import com.shishuhao.OnlineJudge.model.dto.questionSubmit.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);

}
