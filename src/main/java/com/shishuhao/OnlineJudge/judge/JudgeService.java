package com.shishuhao.OnlineJudge.judge;

import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;

public interface JudgeService {
    QuestionSubmit doJudge (long questionId);
}
