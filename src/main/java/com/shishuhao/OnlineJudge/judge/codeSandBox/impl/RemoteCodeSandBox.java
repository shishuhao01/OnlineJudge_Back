package com.shishuhao.OnlineJudge.judge.codeSandBox.impl;

import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeRequest;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeResponse;

/**
 * 远程代码沙箱
 */
public class RemoteCodeSandBox implements CodeSandBox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }
}
