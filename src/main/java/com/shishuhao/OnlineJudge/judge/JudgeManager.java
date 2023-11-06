package com.shishuhao.OnlineJudge.judge;

import com.shishuhao.OnlineJudge.judge.strategy.DefaultJudgeStrategy;
import com.shishuhao.OnlineJudge.judge.strategy.JavaLanguageJudgeStrategy;
import com.shishuhao.OnlineJudge.judge.strategy.JudgeContext;
import com.shishuhao.OnlineJudge.judge.strategy.JudgeStrategy;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.JudgeInfo;
import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 尽量简化对判题功能的调用
 */
@Service
public class JudgeManager {

    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (language.equals("java")) {
            judgeStrategy =  new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);

    }
}
