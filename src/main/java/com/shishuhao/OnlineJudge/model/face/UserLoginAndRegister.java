package com.shishuhao.OnlineJudge.model.face;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserLoginAndRegister {

    private Double confidence;

    private String faceToken;

}
