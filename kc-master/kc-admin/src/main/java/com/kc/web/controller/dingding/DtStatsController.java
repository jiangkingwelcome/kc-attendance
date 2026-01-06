package com.kc.web.controller.dingding;

import com.kc.common.core.controller.BaseController;
import com.kc.common.core.domain.AjaxResult;
import com.kc.dingtalk.service.IStatsCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页统计数据Controller
 * 提供缓存的统计数据，提升首页加载速度
 *
 * @author kc
 * @date 2026-01-06
 */
@RestController
@RequestMapping("/dingtalk/stats")
public class DtStatsController extends BaseController
{
    @Autowired
    private IStatsCacheService statsCacheService;

    /**
     * 获取所有首页统计数据
     * 从Redis缓存获取，避免每次查询数据库
     *
     * @return 统计数据
     */
    @GetMapping("/dashboard")
    public AjaxResult getDashboardStats()
    {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeEmployeeCount", statsCacheService.getActiveEmployeeCount());
        stats.put("attendanceCount", statsCacheService.getAttendanceCount());
        stats.put("todayLeaveCount", statsCacheService.getTodayLeaveCount());
        stats.put("newHireCount", statsCacheService.getThisMonthNewHireCount());
        stats.put("last7DaysAttendance", statsCacheService.getLast7DaysAttendanceStats());
        return AjaxResult.success(stats);
    }

    /**
     * 获取在岗职工数量
     */
    @GetMapping("/activeEmployeeCount")
    public AjaxResult getActiveEmployeeCount()
    {
        return AjaxResult.success(statsCacheService.getActiveEmployeeCount());
    }

    /**
     * 获取考勤记录总数
     */
    @GetMapping("/attendanceCount")
    public AjaxResult getAttendanceCount()
    {
        return AjaxResult.success(statsCacheService.getAttendanceCount());
    }

    /**
     * 获取今日请假人数
     */
    @GetMapping("/todayLeaveCount")
    public AjaxResult getTodayLeaveCount()
    {
        return AjaxResult.success(statsCacheService.getTodayLeaveCount());
    }

    /**
     * 获取本月新入职人数
     */
    @GetMapping("/newHireCount")
    public AjaxResult getThisMonthNewHireCount()
    {
        return AjaxResult.success(statsCacheService.getThisMonthNewHireCount());
    }

    /**
     * 手动刷新所有统计数据缓存
     */
    @GetMapping("/refresh")
    public AjaxResult refreshStats()
    {
        statsCacheService.refreshAllStats();
        return AjaxResult.success("统计数据缓存已刷新");
    }
}
