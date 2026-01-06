package com.kc.dingtalk.service.impl;

import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.DtHoliday;
import com.kc.common.core.redis.RedisCache;
import com.kc.common.utils.DateUtils;
import com.kc.dingtalk.mapper.DtAttendanceAnalyzeMapper;
import com.kc.dingtalk.mapper.DtAttendanceMapper;
import com.kc.dingtalk.mapper.DtEmployeeMapper;
import com.kc.dingtalk.mapper.DtLeaveMapper;
import com.kc.dingtalk.service.IStatsCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 统计数据缓存Service实现类
 * 用于缓存首页统计数据，减少数据库查询压力
 *
 * @author kc
 * @date 2026-01-06
 */
@Service
public class StatsCacheServiceImpl implements IStatsCacheService
{
    private static final Logger log = LoggerFactory.getLogger(StatsCacheServiceImpl.class);

    /** Redis缓存key前缀 */
    private static final String CACHE_KEY_PREFIX = "stats:";

    /** 在岗职工数量缓存key */
    private static final String KEY_ACTIVE_EMPLOYEE_COUNT = CACHE_KEY_PREFIX + "active_employee_count";

    /** 考勤记录总数缓存key */
    private static final String KEY_ATTENDANCE_COUNT = CACHE_KEY_PREFIX + "attendance_count";

    /** 今日请假人数缓存key */
    private static final String KEY_TODAY_LEAVE_COUNT = CACHE_KEY_PREFIX + "today_leave_count";

    /** 本月新入职人数缓存key */
    private static final String KEY_THIS_MONTH_NEW_HIRE_COUNT = CACHE_KEY_PREFIX + "this_month_new_hire_count";

    /** 近七日考勤统计数据缓存key */
    private static final String KEY_LAST_7_DAYS_ATTENDANCE_STATS = CACHE_KEY_PREFIX + "last_7_days_attendance_stats";

    /** 近七日考勤异常趋势数据缓存key */
    private static final String KEY_LAST_7_DAYS_ABNORMAL_STATS = CACHE_KEY_PREFIX + "last_7_days_abnormal_stats";

    /** 缓存过期时间 (35分钟，比定时任务30分钟稍长一点) */
    private static final Integer CACHE_EXPIRE_MINUTES = 35;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DtEmployeeMapper dtEmployeeMapper;

    @Autowired
    private DtAttendanceMapper dtAttendanceMapper;

    @Autowired
    private DtLeaveMapper dtLeaveMapper;

    @Autowired
    private DtAttendanceAnalyzeMapper dtAttendanceAnalyzeMapper;

    @Autowired
    private com.kc.dingtalk.mapper.DtHolidayMapper dtHolidayMapper;

    /**
     * 获取在岗职工数量
     * 优先从Redis缓存获取，缓存不存在时计算并缓存
     */
    @Override
    public int getActiveEmployeeCount()
    {
        // 尝试从缓存获取
        Integer cachedValue = redisCache.getCacheObject(KEY_ACTIVE_EMPLOYEE_COUNT);
        if (cachedValue != null)
        {
            log.debug("从缓存获取在岗职工数量: {}", cachedValue);
            return cachedValue;
        }

        // 缓存不存在，重新计算
        log.info("缓存未命中，重新计算在岗职工数量");
        refreshActiveEmployeeCount();

        // 再次从缓存获取
        cachedValue = redisCache.getCacheObject(KEY_ACTIVE_EMPLOYEE_COUNT);
        return cachedValue != null ? cachedValue : 0;
    }

    /**
     * 获取考勤记录总数
     */
    @Override
    public long getAttendanceCount()
    {
        Long cachedValue = redisCache.getCacheObject(KEY_ATTENDANCE_COUNT);
        if (cachedValue != null)
        {
            log.debug("从缓存获取考勤记录总数: {}", cachedValue);
            return cachedValue;
        }

        log.info("缓存未命中，重新计算考勤记录总数");
        refreshAttendanceCount();

        cachedValue = redisCache.getCacheObject(KEY_ATTENDANCE_COUNT);
        return cachedValue != null ? cachedValue : 0L;
    }

    /**
     * 获取今日请假人数
     */
    @Override
    public int getTodayLeaveCount()
    {
        Integer cachedValue = redisCache.getCacheObject(KEY_TODAY_LEAVE_COUNT);
        if (cachedValue != null)
        {
            log.debug("从缓存获取今日请假人数: {}", cachedValue);
            return cachedValue;
        }

        log.info("缓存未命中，重新计算今日请假人数");
        refreshTodayLeaveCount();

        cachedValue = redisCache.getCacheObject(KEY_TODAY_LEAVE_COUNT);
        return cachedValue != null ? cachedValue : 0;
    }

    /**
     * 获取本月新入职人数
     */
    @Override
    public int getThisMonthNewHireCount()
    {
        Integer cachedValue = redisCache.getCacheObject(KEY_THIS_MONTH_NEW_HIRE_COUNT);
        if (cachedValue != null)
        {
            log.debug("从缓存获取本月新入职人数: {}", cachedValue);
            return cachedValue;
        }

        log.info("缓存未命中，重新计算本月新入职人数");
        refreshThisMonthNewHireCount();

        cachedValue = redisCache.getCacheObject(KEY_THIS_MONTH_NEW_HIRE_COUNT);
        return cachedValue != null ? cachedValue : 0;
    }

    /**
     * 刷新所有统计数据缓存
     */
    @Override
    public void refreshAllStats()
    {
        log.info("开始刷新所有统计数据缓存");
        long startTime = System.currentTimeMillis();

        refreshActiveEmployeeCount();
        refreshAttendanceCount();
        refreshTodayLeaveCount();
        refreshThisMonthNewHireCount();
        refreshLast7DaysAttendanceStats();

        long endTime = System.currentTimeMillis();
        log.info("所有统计数据缓存刷新完成，耗时: {}ms", (endTime - startTime));
    }

    /**
     * 刷新在岗职工数量缓存
     */
    @Override
    public void refreshActiveEmployeeCount()
    {
        try
        {
            // 查询所有员工
            EmployeeDao query = new EmployeeDao();
            List<DtEmployee> allEmployees = dtEmployeeMapper.selectDtEmployeeListNoAuth(query);

            // 过滤在岗员工（没有离职日期的）
            long activeCount = allEmployees.stream()
                .filter(emp -> emp.getLastWorkDay() == null)
                .count();

            // 缓存结果
            redisCache.setCacheObject(KEY_ACTIVE_EMPLOYEE_COUNT, (int) activeCount, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.info("在岗职工数量缓存已更新: {}", activeCount);
        }
        catch (Exception e)
        {
            log.error("刷新在岗职工数量缓存失败", e);
        }
    }

    /**
     * 刷新考勤记录总数缓存
     */
    @Override
    public void refreshAttendanceCount()
    {
        try
        {
            // 直接调用mapper的count方法
            long count = dtAttendanceMapper.countAttendanceRecords();

            // 缓存结果
            redisCache.setCacheObject(KEY_ATTENDANCE_COUNT, count, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.info("考勤记录总数缓存已更新: {}", count);
        }
        catch (Exception e)
        {
            log.error("刷新考勤记录总数缓存失败", e);
        }
    }

    /**
     * 刷新今日请假人数缓存
     */
    @Override
    public void refreshTodayLeaveCount()
    {
        try
        {
            // 获取今天的日期范围
            String today = DateUtils.parseDateToStr("yyyy-MM-dd", new Date());
            int count = dtLeaveMapper.countTodayLeave(today);

            // 缓存结果
            redisCache.setCacheObject(KEY_TODAY_LEAVE_COUNT, count, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.info("今日请假人数缓存已更新: {}", count);
        }
        catch (Exception e)
        {
            log.error("刷新今日请假人数缓存失败", e);
        }
    }

    /**
     * 刷新本月新入职人数缓存
     */
    @Override
    public void refreshThisMonthNewHireCount()
    {
        try
        {
            // 获取本月第一天
            String firstDayOfMonth = DateUtils.parseDateToStr("yyyy-MM", new Date()) + "-01";
            int count = dtEmployeeMapper.countNewHiresSince(firstDayOfMonth);

            // 缓存结果
            redisCache.setCacheObject(KEY_THIS_MONTH_NEW_HIRE_COUNT, count, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.info("本月新入职人数缓存已更新: {}", count);
        }
        catch (Exception e)
        {
            log.error("刷新本月新入职人数缓存失败", e);
        }
    }

    /**
     * 获取近七日考勤统计数据
     */
    @Override
    public List<Map<String, Object>> getLast7DaysAttendanceStats()
    {
        // 尝试从缓存获取
        List<Map<String, Object>> cachedValue = redisCache.getCacheObject(KEY_LAST_7_DAYS_ATTENDANCE_STATS);
        if (cachedValue != null)
        {
            log.debug("从缓存获取近七日考勤统计数据");
            return cachedValue;
        }

        // 缓存不存在，重新计算
        log.info("缓存未命中，重新计算近七日考勤统计数据");
        refreshLast7DaysAttendanceStats();

        // 再次从缓存获取
        cachedValue = redisCache.getCacheObject(KEY_LAST_7_DAYS_ATTENDANCE_STATS);
        return cachedValue;
    }

    /**
     * 刷新近七日考勤统计数据缓存
     */
    @Override
    public void refreshLast7DaysAttendanceStats()
    {
        try
        {
            // 查询近七日考勤统计数据
            List<Map<String, Object>> stats = dtAttendanceMapper.selectLast7DaysAttendanceStats();

            // 缓存结果
            redisCache.setCacheObject(KEY_LAST_7_DAYS_ATTENDANCE_STATS, stats, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.info("近七日考勤统计数据缓存已更新，数据量: {}", stats != null ? stats.size() : 0);
        }
        catch (Exception e)
        {
            log.error("刷新近七日考勤统计数据缓存失败", e);
        }
    }

    /**
     * 获取近七日考勤异常趋势数据
     * 优先从Redis缓存获取，缓存不存在时计算并缓存
     */
    @Override
    public List<Map<String, Object>> getLast7DaysAbnormalStats()
    {
        // 尝试从缓存获取
        List<Map<String, Object>> cachedValue = redisCache.getCacheObject(KEY_LAST_7_DAYS_ABNORMAL_STATS);
        if (cachedValue != null)
        {
            log.debug("从缓存获取近七日考勤异常趋势数据");
            return cachedValue;
        }

        // 缓存不存在，重新计算
        log.info("缓存未命中，重新计算近七日考勤异常趋势数据");
        refreshLast7DaysAbnormalStats();

        // 再次从缓存获取
        cachedValue = redisCache.getCacheObject(KEY_LAST_7_DAYS_ABNORMAL_STATS);
        return cachedValue != null ? cachedValue : new ArrayList<>();
    }

    /**
     * 刷新近七日考勤异常趋势数据缓存
     */
    @Override
    public void refreshLast7DaysAbnormalStats()
    {
        try
        {
            List<Map<String, Object>> result = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat displaySdf = new SimpleDateFormat("MM-dd");

            // 统计近7天的考勤异常数据
            for (int i = 6; i >= 0; i--)
            {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -i);
                Date targetDate = cal.getTime();

                String batchNo = sdf.format(targetDate);
                String displayDate = displaySdf.format(targetDate);

                // 查询该日期的异常数量
                int abnormalCount = dtAttendanceAnalyzeMapper.countAbnormalByDate(batchNo);

                // 查询该日期的异常明细
                Map<String, Object> abnormalDetails = dtAttendanceAnalyzeMapper.countAbnormalDetailsByDate(batchNo);

                // 查询该日期的holiday信息
                DtHoliday holiday = dtHolidayMapper.selectDtHolidayByCurDay(batchNo);

                // 查询该日期是否有实际打卡数据
                int attendanceCount = dtAttendanceMapper.countAttendanceByBatchNo(batchNo);

                Map<String, Object> item = new HashMap<>();
                item.put("date", batchNo);
                item.put("day", displayDate);
                item.put("abnormalCount", abnormalCount);

                // 添加异常明细（缺卡、迟到早退）
                if (abnormalDetails != null) {
                    item.put("missingPunch", abnormalDetails.get("missingPunch") != null ? abnormalDetails.get("missingPunch") : 0);
                    item.put("lateOrEarly", abnormalDetails.get("lateOrEarly") != null ? abnormalDetails.get("lateOrEarly") : 0);
                } else {
                    item.put("missingPunch", 0);
                    item.put("lateOrEarly", 0);
                }

                // 添加日期类型信息
                if (holiday != null) {
                    item.put("dayType", holiday.getDayType());
                    item.put("weekDay", holiday.getWeekDay());
                    item.put("holiday", holiday.getHoliday());
                } else {
                    item.put("dayType", null);
                    item.put("weekDay", null);
                    item.put("holiday", "无");
                }

                // 添加是否有打卡数据标识
                item.put("hasAttendanceData", attendanceCount > 0);

                result.add(item);
            }

            // 缓存结果
            redisCache.setCacheObject(KEY_LAST_7_DAYS_ABNORMAL_STATS, result, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.info("近七日考勤异常趋势数据缓存已更新，数据量: {}", result.size());
        }
        catch (Exception e)
        {
            log.error("刷新近七日考勤异常趋势数据缓存失败", e);
        }
    }
}
