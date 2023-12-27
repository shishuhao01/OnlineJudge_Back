package com.shishuhao.OnlineJudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shishuhao.OnlineJudge.mapper.UserCompetitionRecordMapper;
import com.shishuhao.OnlineJudge.model.entity.UserCompetitionRecord;
import com.shishuhao.OnlineJudge.service.UserCompetitionRecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户比赛记录表 服务实现类
 * </p>
 *
 * @author 豪哥
 * @since 2023-12-20
 */
@Service
public class UserCompetitionRecordServiceImpl extends ServiceImpl<UserCompetitionRecordMapper, UserCompetitionRecord> implements UserCompetitionRecordService {

    @Override
    public QueryWrapper<UserCompetitionRecord> setCompetitionTimeWrapper(long competitionId, long userId) {
        QueryWrapper<UserCompetitionRecord> userCompetitionRecordQueryWrapper = new QueryWrapper<>();
        userCompetitionRecordQueryWrapper.eq("competitionId", competitionId);
        userCompetitionRecordQueryWrapper.eq("userId", userId);
        return userCompetitionRecordQueryWrapper;
    }
}
