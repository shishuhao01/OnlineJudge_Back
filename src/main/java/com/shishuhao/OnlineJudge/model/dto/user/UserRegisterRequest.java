package com.shishuhao.OnlineJudge.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;
    //用户账户
    private String userAccount;

    private String userPassword;

    private String checkPassword;
    //用户名
    private String userName;
}
