package com.kc.dingtalk.service;

import java.util.Date;

public interface ISynLeaveService {

    /**
     * @description:  处理请假数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/4 11:09
     */
    void leaveOrderHandle(Date startDateTime, Date endDateTime,Integer companyType);

}
