package com.shishuhao.OnlineJudge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户比赛记录表
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserCompetitionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户比赛记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 比赛id
     */
    @TableField("competitionId")
    private Long competitionId;

    /**
     * 用户id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 用户姓名
     */
    @TableField("userName")
    private String userName;

    /**
     * 做出题目id
     */
    @TableField("questionAccepted")
    private String questionAccepted;

    /**
     * 用户比赛时长
     */
    @TableField("totalTime")
    private String totalTime;

    /**
     * 用户的得分
     */
    @TableField("Score")
    private Integer score;

    /**
     * 比赛评价
     */
    private String evaluate;

    /**
     * 比赛排名
     */
    private Integer ranking;

    /**
     * 比赛日期
     */
    @TableField("competitionTime")
    private LocalDateTime competitionTime;

    /**
     * 是否删除
     */
    @TableField("isDelete")
    private Integer isDelete;


}
