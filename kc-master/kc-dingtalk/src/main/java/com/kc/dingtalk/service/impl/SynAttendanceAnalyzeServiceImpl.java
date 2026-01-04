package com.kc.dingtalk.service.impl;

import com.kc.common.core.domain.dao.AttendanceDao;
import com.kc.common.core.domain.dao.GetListByParamsResults;
import com.kc.common.core.domain.entity.DtAttendanceAnalyze;
import com.kc.common.enums.AttendanceAnalyzeStatus;
import com.kc.common.enums.YesOrNo;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.mapper.DtAttendanceAnalyzeMapper;
import com.kc.dingtalk.mapper.DtAttendanceMapper;
import com.kc.dingtalk.service.ISynAttendanceAnalyzeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 同步钉钉员工基础信息Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
@Service
public class SynAttendanceAnalyzeServiceImpl implements ISynAttendanceAnalyzeService
{
    private static Logger logger = LoggerFactory.getLogger(SynAttendanceAnalyzeServiceImpl.class);

    //标准工时
    private double standardWorkTime = 10.00;
    @Autowired
    private DtAttendanceAnalyzeMapper dtAttendanceAnalyzeMapper;
    @Autowired
    private DtAttendanceMapper dtAttendanceMapper;

    @Override
    public void synAttendanceAnalyze(String startDateTime,String endDateTime) {
        if(StringUtils.isBlank(startDateTime)){
            //如果开始时间为空，那么从昨天开始
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DAY_OF_MONTH,-1);
            Date yesterday = instance.getTime();
            startDateTime = DateUtils.parseDateToStr("yyyy-MM-dd",yesterday);
        }

        if(StringUtils.isBlank(endDateTime)){
            //如果结束时间为空，那么从今天
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DAY_OF_MONTH,0);
            Date nowDay = instance.getTime();
            endDateTime = DateUtils.parseDateToStr("yyyy-MM-dd",nowDay);
        }

        String s = startDateTime.replace("-","");
        String e = endDateTime.replace("-","");
        dtAttendanceAnalyzeMapper.deleteByBatchNoRange(s,e);
        Date beginDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD,startDateTime);
        Date endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD,endDateTime);
        List<Date> dates = new ArrayList<>();
        while (!beginDate.after(endDate)){
            dates.add(beginDate);
            Calendar instance = Calendar.getInstance();
            instance.setTime(beginDate);
            instance.add(Calendar.DATE, 1);
            beginDate = instance.getTime();
        }
        List<List<Date>> partition = ListUtils.partition(dates, 30);
        for (List<Date> dateList : partition) {
            String batchNo1 = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dateList.get(0)).replace("-","");
            String batchNo2 = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dateList.get(dateList.size()-1)).replace("-","");
            //汇总打卡、考勤、补卡、加班、请假,出差数据
            AttendanceDao dao = new AttendanceDao();
            dao.setBeginBatchNo(batchNo1);
            dao.setEndBatchNo(batchNo2);
            List<GetListByParamsResults> results = dtAttendanceMapper.getListByParams(dao);
            if (CollectionUtils.isEmpty(results)){return;}
            //根据钉钉考勤数据同步到钉钉考勤数据汇总表
            try {
                insertAttendanceAnalyze(results);
            } catch (Exception a) {
                throw new RuntimeException(a);
            }
        }
    }
    private void insertAttendanceAnalyze(List<GetListByParamsResults> results){
        if (CollectionUtils.isEmpty(results)){return;}
        List<DtAttendanceAnalyze> save = new ArrayList<>();
        for (GetListByParamsResults result : results) {
            DtAttendanceAnalyze attendanceAnalyze = new DtAttendanceAnalyze();
            attendanceAnalyze.setEmployeeId(result.getUserId());
            attendanceAnalyze.setName(result.getName());
            attendanceAnalyze.setJobNumber(result.getJobNumber());
            if (StringUtils.startsWithIgnoreCase(result.getJobNumber(),"i")){
                attendanceAnalyze.setEmpType(0); //实习生
            }else if (StringUtils.startsWithIgnoreCase(result.getJobNumber(),"v")){
                attendanceAnalyze.setEmpType(1); //外包
            }

            //设置请假记录
            attendanceAnalyze.setLeaveRecord(result.getLeave());
            attendanceAnalyze.setLeaveDuration(result.getLeaveDuration());
            attendanceAnalyze.setLeaveCount(result.getLeaveCount());
            //设置出差记录
            attendanceAnalyze.setTravelRecord(result.getTravel());
            BigDecimal travelDuration = result.getTravelDuration() == null?BigDecimal.ZERO:new BigDecimal(result.getTravelDuration());
            if (travelDuration.compareTo(new BigDecimal("10")) >=1){
                attendanceAnalyze.setTravelDuration("10.00");
            }else{
                attendanceAnalyze.setTravelDuration(result.getTravelDuration());
            }
            attendanceAnalyze.setTravelCount(result.getTravelCount());
            String batchNo = result.getBatchNo();
            attendanceAnalyze.setWorkTime(result.getWorkTime());
            attendanceAnalyze.setOffWorkTime(result.getOffWorkTime());
            attendanceAnalyze.setDelFlag(YesOrNo.否_0.getCode());
            attendanceAnalyze.setOvertimeType(result.getOvertimeType());
            attendanceAnalyze.setOvertimeDurationDay(result.getOvertimeDurationDay());
            attendanceAnalyze.setOvertimeDurationHour(result.getOvertimeDurationHour());
            if (StringUtils.isNotBlank(result.getPlace())){ //设置上下班地址
                String[] split = StringUtils.split(result.getPlace(), "|");
                String onWorkPlace = split[0]; //上班地址
                String offWorkPlace = split[split.length-1]; //下班地址
                String place = onWorkPlace+"|"+offWorkPlace;
                attendanceAnalyze.setPlace(place);
            }
            //设置时间差
            double duration = calcDuration(attendanceAnalyze.getWorkTime(),attendanceAnalyze.getOffWorkTime());
            //满足标准工时为正常
            if (duration >= standardWorkTime){
                attendanceAnalyze.setStatus(AttendanceAnalyzeStatus.正常_0.getCode());
            }else{
                int dayOfWeek = getDayOfWeek(batchNo);
                Integer count = result.getAttendanceCount();
                if ((dayOfWeek != 1 && dayOfWeek != 7) || count == 0){
                    //工作日工时不足或者没打过卡
                    attendanceAnalyze.setStatus(AttendanceAnalyzeStatus.异常_1.getCode());
                }else if (count >1){
                    //如果是周六日工时不足但是打卡多次也正常
                   attendanceAnalyze.setStatus(AttendanceAnalyzeStatus.正常_0.getCode());
                }else if (dayOfWeek == 1){
                    //周日工时不足且只打卡一次
                    attendanceAnalyze.setStatus(AttendanceAnalyzeStatus.周日仅打一次卡_3.getCode());
                }else if(dayOfWeek == 7){
                    //周六工时不足且只打卡一次
                    attendanceAnalyze.setStatus(AttendanceAnalyzeStatus.周六仅打一次卡_2.getCode());
                }
            }
            attendanceAnalyze.setDuration(String.valueOf(duration));
            attendanceAnalyze.setBatchNo(batchNo);
            save.add(attendanceAnalyze);
        }
        if (CollectionUtils.isNotEmpty(save)){
            dtAttendanceAnalyzeMapper.batchInsertDtAttendanceAnalyze(save);
        }
    }

    /**
     * @description: 计算时间差
     * @param s 开始时间
     * @param e 结束时间
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/16 11:12
     */
    private double calcDuration(Date s,Date e){
        if (s == null || e == null){
            return 0;
        }
        long times = e.getTime() - s.getTime();
        double hours = (double) times/(60*60*1000);
        BigDecimal a= BigDecimal.valueOf(hours);
        return a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * @description:  根据日期批次号  (1-7 代表周日到周六)
     * @param batchNo 日期批次号
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/24 18:52
     */
    private int getDayOfWeek(String batchNo){
        Date date = null;
        try {
            date =  org.apache.commons.lang3.time.DateUtils.parseDate(batchNo,"yyyyMMdd");
        } catch (ParseException e) {
            logger.info("时间解析错误!");
            throw new RuntimeException(e);
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance.get(Calendar.DAY_OF_WEEK);
    }
}
