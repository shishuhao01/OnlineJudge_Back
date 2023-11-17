package com.shishuhao.OnlineJudge.model.dto.competition;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class CompetitionAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 比赛题目
     */
    @TableField("competitionTitle")
    private String competitionTitle;

    /**
     * 比赛描述
     */
    @TableField("competitionContext")
    private String competitionContext;


    /**
     * 题目集合（JSON数组）
     */
    private List<String> question;


    /**
     * 开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime endTime;



}
