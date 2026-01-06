package com.kc.dingtalk.service;

import java.util.List;
import java.util.Map;

/**
 * 统计数据缓存Service接口
 * 用于缓存首页统计数据，减少数据库查询压力
 *
 * @author kc
 * @date 2026-01-06
 */
public interface IStatsCacheService
{
    /**
     * 获取在岗职工数量
     * 从Redis缓存中获取，如果不存在则计算并缓存
     *
     * @return 在岗职工数量
     */
    int getActiveEmployeeCount();

    /**
     * 获取考勤记录总数
     * 从Redis缓存中获取，如果不存在则计算并缓存
     *
     * @return 考勤记录总数
     */
    long getAttendanceCount();

    /**
     * 获取今日请假人数
     * 从Redis缓存中获取，如果不存在则计算并缓存
     *
     * @return 今日请假人数
     */
    int getTodayLeaveCount();

    /**
     * 获取本月新入职人数
     * 从Redis缓存中获取，如果不存在则计算并缓存
     *
     * @return 本月新入职人数
     */
    int getThisMonthNewHireCount();

    /**
     * 刷新所有统计数据缓存
     * 重新计算所有统计数据并更新到Redis
     */
    void refreshAllStats();

    /**
     * 刷新在岗职工数量缓存
     */
    void refreshActiveEmployeeCount();

    /**
     * 刷新考勤记录总数缓存
     */
    void refreshAttendanceCount();

    /**
     * 刷新今日请假人数缓存
     */
    void refreshTodayLeaveCount();

    /**
     * 刷新本月新入职人数缓存
     */
    void refreshThisMonthNewHireCount();

    /**
     * 获取近七日考勤统计数据
     * 从Redis缓存中获取，如果不存在则查询并缓存
     *
     * @return 近七日考勤统计数据 [{date: "2026-01-01", day: "01-01", count: 1200}, ...]
     */
    List<Map<String, Object>> getLast7DaysAttendanceStats();

    /**
     * 刷新近七日考勤统计数据缓存
     */
    void refreshLast7DaysAttendanceStats();
}
