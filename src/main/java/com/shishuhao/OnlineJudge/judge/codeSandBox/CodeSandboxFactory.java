package com.shishuhao.OnlineJudge.judge.codeSandBox;

import com.shishuhao.OnlineJudge.judge.codeSandBox.impl.ExampleCodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.impl.RemoteCodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.impl.ThirdPartyCodeSandBox;

/**
 * 代码沙箱工厂
 */
public class CodeSandboxFactory {

    public static CodeSandBox newInstance (String type) {
        switch (type) {
            case  "remote":
                return new RemoteCodeSandBox();
            case "ThirdPart":
                return new ThirdPartyCodeSandBox();
            default:
                return new ExampleCodeSandBox();
        }
    }


}
