package com.shishuhao.OnlineJudge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shishuhao.OnlineJudge.model.dto.competition.CompetitionQueryRequest;
import com.shishuhao.OnlineJudge.model.entity.Competition;
import com.shishuhao.OnlineJudge.model.vo.CompetitionVO;

/**
 * <p>
 * 比赛表 服务类
 * </p>
 *
 * @author 豪哥
 * @since 2023-11-15
 */
public interface CompetitionService extends IService<Competition> {

      QueryWrapper<Competition> getCompetitionQueryWrapper (CompetitionQueryRequest competitionQueryRequest);

      Page<CompetitionVO> getCompetitionVOPage(Page<Competition> page);
}
