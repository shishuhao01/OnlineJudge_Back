package com.shishuhao.OnlineJudge.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shishuhao.OnlineJudge.common.BaseResponse;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.common.ResultUtils;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.model.dto.competition.CompetitionAddRequest;
import com.shishuhao.OnlineJudge.model.dto.competition.CompetitionQueryRequest;
import com.shishuhao.OnlineJudge.model.entity.Competition;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.vo.CompetitionVO;
import com.shishuhao.OnlineJudge.service.CompetitionService;
import com.shishuhao.OnlineJudge.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 比赛表 前端控制器
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {
    @Resource
    private UserService userService;
    @Resource
    private CompetitionService competitionService;

    @PostMapping("/create")
    public BaseResponse<Long> addCompetition(@RequestBody CompetitionAddRequest competitionAddRequest,
                                             HttpServletRequest httpServletRequest) {
        if (competitionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        if (loginUser == null || !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足，请登录");
        }
        Competition competition = new Competition();
        BeanUtils.copyProperties(competitionAddRequest, competition);
        List<String> question = competitionAddRequest.getQuestion();
        if (question != null) {
            String questionStr = question.toString();
            competition.setQuestion(questionStr);
        }
        competition.setAdminId(loginUser.getId());
        competition.setTotalScore(100);
        long adminId = loginUser.getId();
        boolean save = competitionService.save(competition);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统错误");
        }
        return ResultUtils.success(adminId);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<CompetitionVO>> listCompetitionVOBYyPage(@RequestBody CompetitionQueryRequest competitionQueryRequest,
                                                                      HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "请登录");
        }

        QueryWrapper<Competition> queryWrapper = competitionService.getCompetitionQueryWrapper(competitionQueryRequest);
        long pageNum = competitionQueryRequest.getPageNum();
        long pageSize = competitionQueryRequest.getPageSize();

        Page<Competition> page = new Page<>(pageNum, pageSize);
        competitionService.page(page, queryWrapper);
        return ResultUtils.success(competitionService.getCompetitionVOPage(page));
    }

    @GetMapping("/get")
    public BaseResponse<CompetitionVO> getCompetitionById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Competition competition = competitionService.getById(id);
        if (competition == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛已经过期或者被删除");
        }
        CompetitionVO competitionById = competitionService.getCompetitionById(competition);

        if (competitionById == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求比赛无效");
        }
        return ResultUtils.success(competitionById);

    }


}

