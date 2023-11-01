package com.shishuhao.OnlineJudge.model.vo;

import cn.hutool.json.JSONUtil;
import com.shishuhao.OnlineJudge.model.dto.questionSubmit.JudgeInfo;
import com.shishuhao.OnlineJudge.model.entity.QuestionSubmit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题目
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QuestionSubmitVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */

    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息（json 对象）
     */

    private JudgeInfo judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */

    private Long questionId;

    /**
     * 创建用户 id
     */

    private Long userId;

    /**
     * 创建时间
     */

    private LocalDateTime createTime;

    /**
     * 更新时间
     */

    private LocalDateTime updateTime;


    /**
     * 提交代码用户信息
     */

    private UserVO userVO;

    /**
     * 提交代码题目信息
     */

    private QuestionVO questionVO;


    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmitVO);
        JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
        if (judgeInfo != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeInfo = questionSubmit.getJudgeInfo();
        if (judgeInfo != null) {
            questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfo, JudgeInfo.class));
        }
        return questionSubmitVO;
    }


}
