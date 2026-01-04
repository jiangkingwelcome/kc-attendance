package com.kc.dingtalk.service;

/**
 * 钉钉打卡记录Service接口
 *
 * @author zhaobo.yang
 * @date 2022-11-05
 */
public interface ISynAttendanceAnalyzeService
{


    /**
     * 汇总钉钉打卡记录汇总
     *
     * @param
     */
     void synAttendanceAnalyze(String begin,String end);
}
