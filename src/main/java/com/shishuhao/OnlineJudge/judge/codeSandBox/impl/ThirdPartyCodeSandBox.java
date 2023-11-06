package com.shishuhao.OnlineJudge.judge.codeSandBox.impl;

import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeRequest;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱++");
        return null;
    }
}
