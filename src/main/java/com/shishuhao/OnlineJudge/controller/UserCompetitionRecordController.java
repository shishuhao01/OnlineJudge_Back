package com.shishuhao.OnlineJudge.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     @PostMapping
     public String test() {
         return "hello";
     }

}

