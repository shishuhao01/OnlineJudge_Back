package com.shishuhao.OnlineJudge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

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
public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
     * 组别
     */
    @TableField("languageType")
    private String languageType;

    /**
     * 创建人id
     */
    @TableField("adminId")
    private Long adminId;

    /**
     * 比赛图片
     */
    @TableField("imgUrl")
    private String imgUrl;


    /**
     * 题目集合
     */
    private String question;

    /**
     * 总分数
     */
    @TableField("totalScore")
    private Integer totalScore;

    /**
     * 创建日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    /**
     * 开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime endTime;

    /**
     * 是否删除
     */
    @TableField("isDelete")
    private Integer isDelete;


}
