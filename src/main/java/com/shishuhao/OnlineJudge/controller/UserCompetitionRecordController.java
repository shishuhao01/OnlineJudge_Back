package com.shishuhao.OnlineJudge.controller;


import com.shishuhao.OnlineJudge.common.BaseResponse;
import com.shishuhao.OnlineJudge.common.ErrorCode;
import com.shishuhao.OnlineJudge.common.ResultUtils;
import com.shishuhao.OnlineJudge.exception.BusinessException;
import com.shishuhao.OnlineJudge.model.dto.userCompetitionRecord.UserCompetitionRecordAddRequest;
import com.shishuhao.OnlineJudge.model.entity.Competition;
import com.shishuhao.OnlineJudge.model.entity.User;
import com.shishuhao.OnlineJudge.model.entity.UserCompetitionRecord;
import com.shishuhao.OnlineJudge.service.CompetitionService;
import com.shishuhao.OnlineJudge.service.UserCompetitionRecordService;
import com.shishuhao.OnlineJudge.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户比赛记录表 前端控制器
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/userCompetitionRecord")
public class UserCompetitionRecordController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private UserCompetitionRecordService userCompetitionRecordService;


    @PostMapping("/create")
    public BaseResponse<Long> addCompetition(@RequestBody UserCompetitionRecordAddRequest userCompetitionRecordAddRequest,
                                             HttpServletRequest httpServletRequest) {
        if (userCompetitionRecordAddRequest == null) {
            throw new RuntimeException("参数为空");
        }

        User loginUser = userService.getLoginUser(httpServletRequest);

        if (loginUser == null) {
            throw new RuntimeException("未登录");
        }
        long userId = loginUser.getId();

        User loginUser2 = userService.getById(userCompetitionRecordAddRequest.getUserId());
        if (loginUser2 == null || !loginUser2.equals(loginUser)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户登录信息出错");
        }


        long competitionId = userCompetitionRecordAddRequest.getCompetitionId();

        Competition competitionServiceById = competitionService.getById(competitionId);

        if (competitionServiceById == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "比赛不存在");
        }

        UserCompetitionRecord userCompetitionRecord = new UserCompetitionRecord();
        BeanUtils.copyProperties(userCompetitionRecordAddRequest, userCompetitionRecord);
        userCompetitionRecord.setCompetitionTime(competitionServiceById.getDate());

        boolean save = userCompetitionRecordService.save(userCompetitionRecord);

        if (!save) {
            throw new RuntimeException("系统错误");
        }
        return ResultUtils.success(userId);
    }

}

