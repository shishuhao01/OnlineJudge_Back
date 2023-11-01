package com.shishuhao.OnlineJudge.model.vo;

import cn.hutool.json.JSONUtil;
import com.shishuhao.OnlineJudge.model.dto.question.JudgeConfig;
import com.shishuhao.OnlineJudge.model.entity.Question;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;


    /**
     * 题目提交数
     */

    private Integer submitNum;

    /**
     * 题目通过数
     */

    private Integer acceptedNum;


    /**
     * 判题配置（json 对象）
     */

    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */

    private Integer thumbNum;

    /**
     * 收藏数
     */

    private Integer favourNum;

    /**
     * 创建用户 id
     */

    private Long userId;

    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 更新时间
     */

    private Date updateTime;

    /**
     * 用户对象封装类
     */
    private UserVO userVo;


    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig judgeConfig = questionVO.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }

        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        List<String> strings = JSONUtil.toList(question.getTags(), String.class);
        questionVO.setTags(strings);
        String judgeConfig = question.getJudgeConfig();
        JudgeConfig judgeConfig1 = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
        questionVO.setJudgeConfig(judgeConfig1);

        return questionVO;
    }


}
