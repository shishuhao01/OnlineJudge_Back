package com.shishuhao.OnlineJudge.judge.codeSandBox.impl;

import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeRequest;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeResponse;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.JudgeInfo;
import com.shishuhao.OnlineJudge.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * 实例代码沙箱
 */
public class ExampleCodeSandBox implements CodeSandBox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        //todo 先给假数据
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        //获取到题目的输入实例
        List<String> outputList = executeCodeRequest.getInputList();
        //假数据放的是执行结果后的输出实例
        executeCodeResponse.setOutputList(outputList);
        executeCodeResponse.setMessage(executeCodeResponse.getMessage());
        executeCodeResponse.setStatus(executeCodeResponse.getStatus());

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemoryLimit(1L);
        judgeInfo.setTime(1L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;

    }
}
