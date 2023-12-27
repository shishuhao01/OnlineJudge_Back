package com.shishuhao.OnlineJudge.model.dto.userCompetitionRecord;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserCompetitionRecordDoQuestionRequest {
    private long competitionId;

    private long questionId;

    private String language;

    private String code;


}
