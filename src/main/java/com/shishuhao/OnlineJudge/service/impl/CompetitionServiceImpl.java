package com.shishuhao.OnlineJudge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shishuhao.OnlineJudge.constant.CommonConstant;
import com.shishuhao.OnlineJudge.mapper.CompetitionMapper;
import com.shishuhao.OnlineJudge.model.dto.competition.CompetitionQueryRequest;
import com.shishuhao.OnlineJudge.model.entity.Competition;
import com.shishuhao.OnlineJudge.model.entity.Question;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.vo.CompetitionVO;
import com.shishuhao.OnlineJudge.model.vo.QuestionVO;
import com.shishuhao.OnlineJudge.model.vo.UserVO;
import com.shishuhao.OnlineJudge.service.CompetitionService;
import com.shishuhao.OnlineJudge.service.QuestionService;
import com.shishuhao.OnlineJudge.service.UserService;
import com.shishuhao.OnlineJudge.utils.SqlUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 比赛表 服务实现类
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {
    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;


    public QueryWrapper<Competition> getCompetitionQueryWrapper(CompetitionQueryRequest competitionQueryRequest) {
        QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
        if (competitionQueryRequest == null) {
            return queryWrapper;
        }

        Long competitionId = competitionQueryRequest.getCompetitionId();
        String competitionTitle = competitionQueryRequest.getCompetitionTitle();
        LocalDate startDate = competitionQueryRequest.getStartDate();
        LocalDate endDate = competitionQueryRequest.getEndDate();
        if (competitionId != null) {
            queryWrapper.eq("id", competitionId);
        }
        queryWrapper.like(StringUtils.isNotBlank(competitionTitle), "competitionTitle", competitionTitle);
        queryWrapper.gt(ObjectUtil.isNotNull(startDate), "date", startDate);
        queryWrapper.lt(ObjectUtil.isNotNull(endDate), "date", endDate);
        queryWrapper.orderByDesc("date");
        String sortField = competitionQueryRequest.getSortField();
        String sortOrder = competitionQueryRequest.getSortOrder();

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    @Override
    public Page<CompetitionVO> getCompetitionVOPage(Page<Competition> page) {
        Page<CompetitionVO> competitionVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Competition> competitionList = page.getRecords();
        List<CompetitionVO> competitionVOList = competitionList.stream().map(competition -> {
            CompetitionVO competitionVO = new CompetitionVO();
            competitionVO.setId(competition.getId());
            competitionVO.setCompetitionTitle(competition.getCompetitionTitle());
            competitionVO.setCompetitionContext(competition.getCompetitionContext());

            User admin = userService.getById(competition.getAdminId());
            competitionVO.setUserVO(userService.getUserVO(admin));

            //todo 优化
            String question = competition.getQuestion();
            // 去除方括号并按逗号分割字符串
            String str = question.replace(" ", "");
            String[] strArray = str.substring(1, str.length() - 1).split(",");
            // 将字符串数组转换为长整型数组
            long[] longArray = new long[strArray.length];
            for (int i = 0; i < strArray.length; i++) {
                longArray[i] = Long.parseLong(strArray[i]);
            }
            // 将长整型数组转换为列表
            List<Long> questionStr = Arrays.asList(ArrayUtils.toObject(longArray));

            List<QuestionVO> questionVOList = new ArrayList<>();
            for (Long questionId : questionStr) {
                Question question3 = questionService.getById(Long.valueOf(questionId));
                if (question3 != null) {
                    questionVOList.add(QuestionVO.objToVo(question3));
                }
            }


            competitionVO.setQuestionVOList(questionVOList);
            competitionVO.setDate(competition.getDate());
            competitionVO.setLanguageType(competition.getLanguageType());
            competitionVO.setTotalScore(competition.getTotalScore());
            competitionVO.setStartTime(competition.getStartTime());
            competitionVO.setEndTime(competition.getEndTime());
            competitionVO.setImgUrl(competition.getImgUrl());
            return competitionVO;
        }).collect(Collectors.toList());
        competitionVOPage.setRecords(competitionVOList);
        return competitionVOPage;
    }

    @Override
    public CompetitionVO getCompetitionById(Competition competition) {
        CompetitionVO competitionVO = new CompetitionVO();
        competitionVO.setId(competition.getId());
        competitionVO.setCompetitionTitle(competition.getCompetitionTitle());
        competitionVO.setCompetitionContext(competition.getCompetitionContext());
        competitionVO.setLanguageType(competitionVO.getLanguageType());

        competitionVO.setTotalScore(competition.getTotalScore());
        competitionVO.setDate(competition.getDate());
        competitionVO.setStartTime(competition.getStartTime());
        competitionVO.setEndTime(competition.getEndTime());

        //根据题目id先查询用户

        Long adminId = competition.getAdminId();
        User user = userService.getById(adminId);
        UserVO userVo = userService.getUserVO(user);
        competitionVO.setUserVO(userVo);

        //查询所有题目信息
        String question = competition.getQuestion();
        // 去除方括号并按逗号分割字符串
        String str = question.replace(" ", "");
        String[] strArray = str.substring(1, str.length() - 1).split(",");
        // 将字符串数组转换为长整型数组
        long[] longArray = new long[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            longArray[i] = Long.parseLong(strArray[i]);
        }
        // 将长整型数组转换为列表
        List<Long> questionStr = Arrays.asList(ArrayUtils.toObject(longArray));
        List<QuestionVO> questionVOList = new ArrayList<>();
        for (Long questionId : questionStr) {
            Question question3 = questionService.getById(Long.valueOf(questionId));
            if (question3 != null) {
                questionVOList.add(QuestionVO.objToVo(question3));
            }
        }
        competitionVO.setQuestionVOList(questionVOList);
        competitionVO.setImgUrl(competition.getImgUrl());


        return competitionVO;
    }
}
