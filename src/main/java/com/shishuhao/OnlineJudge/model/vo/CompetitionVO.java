package com.shishuhao.OnlineJudge.model.vo;

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
public class CompetitionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 比赛题目
     */
    private String competitionTitle;

    /**
     * 比赛描述
     */
    private String competitionContext;

    /**
     * 创建人信息
     */
    private UserVO userVO;

    /**
     * 题目集合
     */
    private List<QuestionVO> questionVOList;

    /**
     * 总分数
     */
    private Integer totalScore;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;




}
