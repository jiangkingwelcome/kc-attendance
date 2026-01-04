package com.kc.dingtalk.service;

public interface ISynMendcardOrderService {

    /**
     * @description:  将补卡数据补全到考勤数据中
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/4 11:09
     */
    void mendcardCompletion(Long startTimeStamp, Long endTimeStamp);



}
