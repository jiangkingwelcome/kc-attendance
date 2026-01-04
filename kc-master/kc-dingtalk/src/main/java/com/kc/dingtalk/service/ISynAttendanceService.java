package com.kc.dingtalk.service;

import java.util.Date;

/**
 * 钉钉打卡记录Service接口
 *
 * @author zhaobo.yang
 * @date 2022-11-05
 */
public interface ISynAttendanceService
{


    /**
     * 同步钉钉打卡记录
     *
     * @param sDate 开始时间（yyyy-MM-dd）
     * @param eDate 结束时间（yyyy-MM-dd）
     * @param companyType 公司类型
     */
     void synAttendance(Date sDate, Date eDate, int companyType);


    /**
     * 同步钉钉签到记录
     *
     * @param company 公司类型
     */
    /**
     * 同步签到记录。
     * 该方法用于将指定日期范围内的签到记录与某个公司进行同步。
     *
     * @param sDate 开始日期，表示需要同步的签到记录的起始日期。
     * @param eDate 结束日期，表示需要同步的签到记录的结束日期。
     * @param company 公司编号，表示需要与之同步签到记录的公司。
     * @return 无返回值。
     */
    void synSignIn(Date sDate, Date eDate,int company);
}
