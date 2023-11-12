package com.shishuhao.OnlineJudge.judge.codeSandBox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.judge.codeSandBox.CodeSandBox;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeRequest;
import com.shishuhao.OnlineJudge.judge.codeSandBox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱
 */
public class RemoteCodeSandBox implements CodeSandBox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8102/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String response = HttpUtil.createPost(url).
                body(json).execute().body();
        if (StringUtils.isBlank(response)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandBox error message={}" + response);
        }
        return JSONUtil.toBean(response, ExecuteCodeResponse.class);
    }

}
