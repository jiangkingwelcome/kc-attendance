package com.kc.quartz.task;

import com.kc.common.core.domain.entity.DtHoliday;
import com.kc.common.enums.CompanyType;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.service.IDtAttendanceAnalyzeService;
import com.kc.dingtalk.service.IDtAttendanceService;
import com.kc.dingtalk.service.IDtHolidayService;
import com.kc.dingtalk.service.IDtOvertimeService;
import com.kc.dingtalk.service.IDtTravelService;
import com.kc.dingtalk.service.IDtWorkdayService;
import com.kc.dingtalk.service.ISynAttendanceAnalyzeService;
import com.kc.dingtalk.service.ISynAttendanceService;
import com.kc.dingtalk.service.ISynDtDeptService;
import com.kc.dingtalk.service.ISynDtEmployeeService;
import com.kc.dingtalk.service.ISynMendcardOrderService;
import com.kc.dingtalk.service.ISynLeaveService;
import com.kc.dingtalk.service.IStatsCacheService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * 定时任务调度测试
 *
 * @author kc
 */
@Component("kcTask")
public class KcTask {
    private static Logger logger = LoggerFactory.getLogger(KcTask.class);
    @Autowired
    private ISynDtDeptService iSynDtDeptService;
    @Autowired
    private ISynDtEmployeeService iSynDtEmployeeService;
    @Autowired
    private ISynAttendanceService iSynAttendanceService;
    @Autowired
    private ISynAttendanceAnalyzeService iSynAttendanceAnalyzeService;
    @Autowired
    private IDtAttendanceAnalyzeService iDtAttendanceAnalyzeService;
    @Autowired
    private IDtAttendanceService iDtAttendanceService;
    @Autowired
    private ISynMendcardOrderService iSynMendcardOrderService;
    @Autowired
    private ISynLeaveService iSynLeaveService;
    @Autowired
    private IDtWorkdayService workdayService;
    @Autowired
    private IDtOvertimeService overtimeService;
    @Autowired
    private IDtHolidayService holidayService;
    @Autowired
    private IDtTravelService travelService;
    @Autowired
    private IStatsCacheService statsCacheService;
    /**
     * @description: 钉钉部门同步
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/5 22:52
     */
    public void synDtDept() {
        //同步钉钉部门表
        iSynDtDeptService.synDtDept();
    }

    /**
     * @description: 钉钉用户同步
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/5 22:52
     */
    public void synDtEmployee() {
        //钉钉人员同步
        iSynDtEmployeeService.synDtEmployee();
        //批量处理人员类型
        iSynDtEmployeeService.handleEmpType();

    }

    /**
     * @param startTime   开始时间(yyyy-MM-dd)
     * @param endDateTime 结束时间(yyyy-MM-dd)
     * @param companyType 公司类别
     * @description: 拉取钉钉用户打卡记录
     * @author: zhaobo.yang
     * @date: 2022/11/5 22:52
     */
    public void synDtAttendance(String startTime, String endDateTime,String companyType) {
        logger.info("【startTime】:" + startTime + "【endDateTime】：" + endDateTime + "【companyType】：" + companyType);
        List<Integer> companyTypes = new ArrayList<>();
        if (StringUtils.isNotBlank(companyType)) {
            String[] split = companyType.split(",");
            for (String item : Arrays.asList(split)) {
                companyTypes.add(Integer.valueOf(item));
            }
        } else { //默认智能、潍坊
            companyTypes.add(CompanyType.快仓智能科技有限公司_1.getCode());
            companyTypes.add(CompanyType.宝仓智能科技苏州有限公司_3.getCode());
        }
        Date sDate = null; //开始时间
        Date eDate = null;  //结束时间
        if (StringUtils.isBlank(endDateTime)) { //如果结束时间为空，结束时间为今天
            eDate = DateUtils.getYYYYMMDD();
        } else {
            eDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endDateTime);
        }
        if (StringUtils.isBlank(startTime)) { //如果开始时间为空 则开始时间为结束时间前两天
            Calendar instance = Calendar.getInstance();
            instance.setTime(eDate);
            instance.add(Calendar.DAY_OF_MONTH, -2);
            sDate = instance.getTime();
        } else {
            sDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, startTime);
        }
        //开始时间和结束时间校验
        if (sDate.after(eDate)) {
            throw new RuntimeException("开始时间必须小于结束时间!");
        }
        int i = DateUtils.differentDaysByMillisecond(sDate, eDate);
        if (i > 180) {
            throw new RuntimeException("开始时间和结束时间相差不能大于180天!");
        }
        for (Integer type : companyTypes) {
            iSynAttendanceService.synAttendance(sDate, eDate,type);
        }
    }

    /**
     * @description: 拉取钉钉用户签到记录
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/5 22:52
     */
    public void synDtSignIn(String startTime, String endTime, String companyType) {
        logger.info("【startTime】:" + startTime + "【endTime】：" + endTime + "【companyType】：" + companyType);
        Date sDate = null; //开始时间
        Date eDate = null;  //结束时间
        if (StringUtils.isBlank(endTime)) { //如果结束时间为空，结束时间为昨天
          /*  Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DAY_OF_MONTH, -1);
            instance.set(Calendar.MILLISECOND, 0);
            eDate = instance.getTime();*/
            eDate = DateUtils.getYYYYMMDD();
        } else {
            eDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endTime);
        }
        if (StringUtils.isBlank(startTime)) {//如果开始时间为空 则开始时间为结束时间前两天
            Calendar instance = Calendar.getInstance();
            instance.setTime(eDate);
            instance.add(Calendar.DAY_OF_MONTH, -2);
            instance.set(Calendar.MILLISECOND, 0);
            sDate = instance.getTime();
        } else {
            sDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, startTime);
        }
        //开始时间和结束时间校验
        if (sDate.after(eDate)) {
            throw new RuntimeException("开始时间必须小于结束时间!");
        }
        int i = DateUtils.differentDaysByMillisecond(sDate, eDate);
        if (i > 180) {
            throw new RuntimeException("开始时间和结束时间相差不能大于180天!");
        }
        List<Integer> companyTypes = new ArrayList<>();
        if (StringUtils.isNotBlank(companyType)) {
            String[] split = companyType.split(",");
            for (String item : Arrays.asList(split)) {
                companyTypes.add(Integer.valueOf(item));
            }
        } else { //默认智能
            companyTypes.add(CompanyType.快仓智能科技有限公司_1.getCode());
        }
        for (Integer type : companyTypes) {
            iSynAttendanceService.synSignIn(sDate, eDate, type);
        }
    }



    /**
     * @param startTime   开始时间(yyyy-MM-dd)
     * @param endDateTime 结束时间(yyyy-MM-dd)
     * @description: 拉取钉钉用户补卡工单
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/07/04 22:52
     */
    public void synMendcardOrder(String startTime, String endDateTime) {
        logger.info("【startTime】:" + startTime + "【endDateTime】：" + endDateTime);
        Date sDate = null; //开始时间
        Date eDate = null;  //结束时间
        if (StringUtils.isBlank(endDateTime)) { //如果结束时间为空，结束时间为昨天
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DAY_OF_MONTH, -1);
            instance.set(Calendar.HOUR_OF_DAY, 23);
            instance.set(Calendar.MINUTE, 59);
            instance.set(Calendar.SECOND, 59);
            instance.set(Calendar.MILLISECOND, 0);
            eDate = instance.getTime();
        } else {
            eDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endDateTime);
        }

        if (StringUtils.isBlank(startTime)) {//如果开始时间为空 则开始时间为结束时间前三十天
            Calendar instance = Calendar.getInstance();
            instance.setTime(eDate);
            instance.add(Calendar.DAY_OF_MONTH, -30);
            instance.set(Calendar.HOUR_OF_DAY, 0);
            instance.set(Calendar.MINUTE, 0);
            instance.set(Calendar.SECOND, 0);
            instance.set(Calendar.MILLISECOND, 0);
            sDate = instance.getTime();
        } else {
            sDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, startTime);
        }

        //校验开始时间和结束时间之差不能超过120天
        int startEnddiffDay = DateUtils.differentDaysByMillisecond(sDate, eDate);
        if (startEnddiffDay > 120) {
            throw new RuntimeException("开始结束时间不能超过120天!");
        }
        //校验开始时间到今天不可超过365
        if (DateUtils.differentDaysByMillisecond(sDate, DateUtils.getYYYYMMDD()) > 365) {
            throw new RuntimeException("开始时间与今天的时间差不能超过365天!");
        }
        //拉取钉钉补卡工单
        iSynMendcardOrderService.mendcardCompletion(sDate.getTime(), eDate.getTime());
    }

    /**
     * @description: 同步钉钉考勤数据到分析汇总表
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/5 22:52
     */
    public void synDtAttendanceAnalyze(String startTime, String endTime) {
        System.out.println("startTime:" + startTime + "endTime:" + endTime);
        iSynAttendanceAnalyzeService.synAttendanceAnalyze(startTime, endTime);
    }

    /**
     * @description: 定时删除六个月前的考勤数据和考勤汇总数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/5 22:52
     */
    public void deleteData(Integer day) {
        if (day == null) {
            day = 180;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DAY_OF_MONTH, day * (-1));
        Date time = instance.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String batchNo = simpleDateFormat.format(time);
        //删除汇总数据
        iDtAttendanceAnalyzeService.deleteAttendanceAnalyze(batchNo);
        //删除打卡和签到数据
        iDtAttendanceService.deleteByBatchNo(batchNo);
    }


    /**
     * @description: 批量创建钉钉用户
     * @author: zhaobo.yang
     * @date: 2022/12/15 15:12
     */
    public void batchCreateUser() {
        iSynDtEmployeeService.batchCreateUser();
    }


    /**
     * @param startTime   开始时间(yyyy-MM-dd)
     * @param endTime 结束时间(yyyy-MM-dd)
     * @description: 拉取钉钉用户请假数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/09/26 21:11
     */
    public void synLeaveOrder(String startTime, String endTime,String companyType) {
        logger.info("填写的参数是【startTime】:" + startTime + "【endTime】:" + endTime+ "【companyType】:" + companyType);
        Date sDate = null; //开始时间
        Date eDate = null;  //结束时间
        if (StringUtils.isBlank(endTime)) { //如果结束时间为空，结束时间为昨天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            eDate = calendar.getTime();
        } else {
            eDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, endTime+" 23:59:59");
        }

        if (StringUtils.isBlank(startTime)) {//如果开始时间为空 则开始时间为结束时间前两天
            Calendar instance = Calendar.getInstance();
            instance.setTime(eDate);
            instance.add(Calendar.DAY_OF_MONTH, -2);
            instance.set(Calendar.HOUR_OF_DAY, 00);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
            sDate = instance.getTime();
        } else {
            sDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, startTime+" 00:00:00");
        }
        //校验开始时间和结束时间之差不能超过180天
        int startEnddiffDay = DateUtils.differentDaysByMillisecond(sDate, eDate);
        if (startEnddiffDay > 180) {
            throw new RuntimeException("开始结束时间不能超过180天!");
        }
        List<Integer> companyTypes = new ArrayList<>();
        if (StringUtils.isNotBlank(companyType)) {
            String[] split = companyType.split(",");
            Iterator<String> iterator = Arrays.stream(split).iterator();
            while (iterator.hasNext()) {
                companyTypes.add(Integer.valueOf(iterator.next()));
            }
        } else { //默认智能、宝仓
            companyTypes.add(CompanyType.快仓智能科技有限公司_1.getCode());
            companyTypes.add(CompanyType.宝仓智能科技苏州有限公司_3.getCode());
        }

        for (Integer type : companyTypes) {
            //拉取钉钉请假记录
            iSynLeaveService.leaveOrderHandle(sDate, eDate,type);
        }
    }

    /**
     * @description:  同步加班数据
     * @author: zhaobo.yang
     * @date: 2023/10/19 17:08
     */
    public void synGetOverTimeList(String startTime,String endTime,String companyType){
        Date startDate;
        Date endDate;
        if (StringUtils.isBlank(endTime)) {
            endDate = DateUtils.getYYYYMMDD(); //当天
        } else {
            endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endTime);
        }
        if (StringUtils.isBlank(startTime)) { //开始时间为空，则开始时间为结束时间前15天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, -15);
            startDate = calendar.getTime();
        } else {
            startDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, startTime);
        }
        if (startDate.after(endDate)) {
            throw new RuntimeException("开始时间不得晚于结束时间");
        }
        int i = DateUtils.differentDaysByMillisecond(startDate, endDate);
        if (i > 30) {
            throw new RuntimeException("开始时间和结束时间不得相差超过30天");
        }
        List<Integer> companyTypes = new ArrayList<>();
        if (StringUtils.isNotBlank(companyType)) {
            String[] split = companyType.split(",");
            Iterator<String> iterator = Arrays.stream(split).iterator();
            while (iterator.hasNext()) {
                companyTypes.add(Integer.valueOf(iterator.next()));
            }
        } else { //默认智能、宝仓
            companyTypes.add(CompanyType.快仓智能科技有限公司_1.getCode());
            companyTypes.add(CompanyType.宝仓智能科技苏州有限公司_3.getCode());
        }
        for (Integer type : companyTypes) {
            overtimeService.synGetOverTimeList(startDate,endDate,type);
        }
    };

    /**
     * @description: 批量创建员工人员日期数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/09/26 21:11
     */
    public void batchCreateEmployeeWorkday(String startTime,String endTime){
        Date startDate;
        Date endDate;
        if (StringUtils.isBlank(endTime)) {
            endDate = DateUtils.getYYYYMMDD(); //当天
        } else {
            endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endTime);
        }
        if (StringUtils.isBlank(startTime)) {
            startDate =  DateUtils.getYYYYMMDD(); //当天
        } else {
            startDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, startTime);
        }
        if (startDate.after(endDate)) {
            throw new RuntimeException("开始时间不得晚于结束时间");
        }
        workdayService.insertDateBy(startDate,endDate);
    };


    /**
     * 查询开始时间和节数时间之间的节假日
     * @param startDate 开始日期，格式为yyyy-MM-dd 表示要插入为工作日的日期。
     * @param endDate 结束日期，格式为yyyy-MM-dd 该参数在当前方法实现中未被使用。
     */
    public void holidayJob(String startDate,String endDate){
        Date sDate;
        Date eDate;
        if (StringUtils.isBlank(endDate)) {
            eDate = DateUtils.getYYYYMMDD(); //今天
        } else {
            eDate = DateUtils.dateTime("yyyy-MM-dd",endDate);
        }
        if (StringUtils.isBlank(startDate)) {
            sDate = DateUtils.getYYYYMMDD(); //今天
        } else {
            sDate = DateUtils.dateTime("yyyy-MM-dd",startDate);
        }
        List<DtHoliday> holidays = new ArrayList<>();
        while (!sDate.after(eDate)){
            String yyyyMMdd = DateUtils.parseDateToStr("yyyyMMdd", sDate);
            DtHoliday dtHoliday = holidayService.queryHoliday(yyyyMMdd);
            if (dtHoliday != null) {
                dtHoliday.setDelFlag("0");
                dtHoliday.setCreateBy("1");
                holidays.add(dtHoliday);
            }
            Calendar instance = Calendar.getInstance();
            instance.setTime(sDate);
            instance.add(Calendar.DATE,1);
            sDate = instance.getTime();
        }
        if (CollectionUtils.isNotEmpty(holidays)){
            holidayService.batchInsert(holidays);
        }
    };

    /**
     * 同步会联易旅途的起始和结束日期
     * @param startTime 最后修改日期开始时间
     * @param endTime  最后修改日期结束时间
     */
    public void synTravelByHuiLianYi(String startTime,String endTime){
        Date startDate;
        Date endDate;
        if (StringUtils.isBlank(endTime)) {
            endDate = DateUtils.getYYYYMMDD(); //当天
        } else {
            endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endTime);
        }
        if (StringUtils.isBlank(startTime)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, -60);
            startDate = calendar.getTime();
        } else {
            startDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, startTime);
        }
        if (startDate.after(endDate)) {
            throw new RuntimeException("开始时间不得晚于结束时间");
        }
        travelService.synTravelByHuiLianYi(startDate,endDate);
    };

    /**
     * 同步员工花名册获取身份证号码，工号，主部门
     * @param  companyType 公司类型
     */
    public void synIdCardAndJobNumAndMainDept(String companyType){
        if (StringUtils.isBlank(companyType)){ //全部同步
            companyType = "1,3";
        }

        List<Integer> companyTypes = new ArrayList<>();
        if (StringUtils.isNotBlank(companyType)) {
            String[] split = companyType.split(",");
            Iterator<String> iterator = Arrays.stream(split).iterator();
            while (iterator.hasNext()) {
                companyTypes.add(Integer.valueOf(iterator.next()));
            }
        }
        for (Integer type : companyTypes) {
            iSynDtEmployeeService.synIdCardAndJobNumAndMainDept(type);
        }
    };

    /**
     * 同步员工离职信息
     * @param  companyType 公司类型
     */
    public void synEmployeeDismissionInfo(String startTime,String endTime,String companyType){
        Date startDate;
        Date endDate;
        if (StringUtils.isBlank(endTime)) {
            endDate = DateUtils.getYYYYMMDD(); //当天
        } else {
            endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endTime);
        }
        if (StringUtils.isBlank(startTime)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, -6);//六天前
            startDate = calendar.getTime();
        } else {
            startDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, startTime);
        }
        if (startDate.after(endDate)) {
            throw new RuntimeException("开始时间不得晚于结束时间");
        }

        //校验开始时间到今天不可超过365
        if (DateUtils.differentDaysByMillisecond(startDate, endDate) > 365) {
            throw new RuntimeException("开始时间与结束时间差不能超过365天!");
        }

        if (StringUtils.isBlank(companyType)){ //全部同步
            companyType = "1,3";
        }

        List<Integer> companyTypes = new ArrayList<>();
        if (StringUtils.isNotBlank(companyType)) {
            String[] split = companyType.split(",");
            Iterator<String> iterator = Arrays.stream(split).iterator();
            while (iterator.hasNext()) {
                companyTypes.add(Integer.valueOf(iterator.next()));
            }
        }
        String startDateStr = DateUtils.convertFromBeijingTimeToUtc(startDate);
        String endDateStr = DateUtils.convertFromBeijingTimeToUtc(endDate);

        for (Integer type : companyTypes) {
            iSynDtEmployeeService.synEmployeeDismissionInfo(startDateStr,endDateStr,type);
        }



    };

    /**
     * 刷新首页统计数据缓存
     * 每30分钟执行一次，减少数据库查询压力
     *
     * @author kc
     * @date 2026-01-06
     */
    public void refreshStatsCache()
    {
        logger.info("开始刷新首页统计数据缓存...");
        try
        {
            statsCacheService.refreshAllStats();
            logger.info("首页统计数据缓存刷新成功");
        }
        catch (Exception e)
        {
            logger.error("刷新首页统计数据缓存失败", e);
        }
    }
}
