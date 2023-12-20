package com.shishuhao.OnlineJudge.model.dto.userCompetitionRecord;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserCompetitionRecordAddRequest {

    private long id;

    private long competitionId;

    private long userId;

    private String userName;


}
