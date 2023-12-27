package com.shishuhao.OnlineJudge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shishuhao.OnlineJudge.model.entity.UserCompetitionRecord;

/**
 * <p>
 * 用户比赛记录表 服务类
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
public interface UserCompetitionRecordService extends IService<UserCompetitionRecord> {
    QueryWrapper<UserCompetitionRecord> setCompetitionTimeWrapper(long competitionId, long userId);

}
