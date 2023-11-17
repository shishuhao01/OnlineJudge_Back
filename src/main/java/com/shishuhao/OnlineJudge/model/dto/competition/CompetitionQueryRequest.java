package com.shishuhao.OnlineJudge.model.dto.competition;

import com.shishuhao.OnlineJudge.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 比赛表
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CompetitionQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 根据比赛id查询题目;
     */
    private Long CompetitionId;

    /**
     * 比赛题目
     */
    private String competitionTitle;


    /**
     * 根据日期查询比赛，
     * 从什么时候开始
     */
    private LocalDate startDate;


    /**
     * 到end截止
     */
    private LocalDate endDate;



}
