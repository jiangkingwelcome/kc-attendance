package com.kc.dingtalk.service.impl;

import com.dingtalk.api.response.OapiAttendanceGetupdatedataResponse;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.DtLeave;
import com.kc.common.core.domain.entity.DtWorkday;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.DingTalkService;
import com.kc.dingtalk.mapper.DtEmployeeMapper;
import com.kc.dingtalk.mapper.DtWorkdayMapper;
import com.kc.dingtalk.service.IDtLeaveService;
import com.kc.dingtalk.service.ISynAttendanceAnalyzeService;
import com.kc.dingtalk.service.ISynLeaveService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2023/7/4 11:14
 */
@Service
public class SynLeaveServiceImpl implements ISynLeaveService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SynLeaveServiceImpl.class);

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 12, 3000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(50), new ThreadPoolExecutor.AbortPolicy());
    @Autowired
    private IDtLeaveService leaveService;
    @Autowired
    private DingTalkService dingTalkService;
    @Autowired
    private DtEmployeeMapper employeeMapper;
    @Autowired
    private ISynAttendanceAnalyzeService iSynAttendanceAnalyzeService;

    @Autowired
    private DtWorkdayMapper dtWorkdayMapper;

    @Override
    public void leaveOrderHandle(Date startDateTime, Date endDateTime, Integer companyType) {
        String endDateTimeBatchNo = DateUtils.parseDateToStr("yyyyMMdd", endDateTime);
        String startDateTimeBatchNo = DateUtils.parseDateToStr("yyyyMMdd", startDateTime);
        long t0 = System.currentTimeMillis();
        //根据公司类型查询人员数据
        EmployeeDao queryDao = new EmployeeDao();
        queryDao.setCompanyType(companyType);
        List<DtEmployee> dtEmployees = employeeMapper.selectDtEmployeeList(queryDao);
        //当前公司类型的所有员工
        List<String> curCompanyAllEmp = dtEmployees.stream().map(DtEmployee::getEmployeeId).collect(Collectors.toList());
        DtWorkday dtWorkday = new DtWorkday();
        dtWorkday.setBeginBatchNo(startDateTimeBatchNo);
        dtWorkday.setEndBatchNo(endDateTimeBatchNo);
        List<DtWorkday> workdays = dtWorkdayMapper.selectDtWorkdayList(dtWorkday);
        if (CollectionUtils.isEmpty(workdays)) {
            return;
        }
        Map<String, String> idAndName = workdays.stream().collect(Collectors.toMap(DtWorkday::getEmployeeId, DtWorkday::getName, (value1, value2) -> value1));
        List<String> employeeIds = new ArrayList<>(idAndName.keySet());
        List<String> filterEmployeeIds = new ArrayList<>();
        for (String employeeId : employeeIds) {
            if (curCompanyAllEmp.contains(employeeId)){
                filterEmployeeIds.add(employeeId);
            }
        }
        employeeIds = filterEmployeeIds;

        if (CollectionUtils.isEmpty(employeeIds)) {
            throw new RuntimeException("人员不可为空!");
        }
        long t1 = System.currentTimeMillis();
        //每条子线程处理数据的数量
        int perCount = 100;
        //拆分集合,每个100
        List<List<String>> partitions = ListUtils.partition(employeeIds, perCount);
        logger.info("批量处理多线程开始,本次处理的人员数量:{}", employeeIds.size());
        logger.info("多线程数量:{}", partitions.size());
        List<CompletableFuture> futures = Lists.newArrayList();
        for (List<String> partition : partitions) {
            List<String> employeeIdList = partition;
            Date sDate = startDateTime;
            Date eDate = endDateTime;
            Integer companyTypeSyn = companyType;
            CompletableFuture<List<OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo>> future =
                    CompletableFuture.supplyAsync(() -> getUpdatedata(employeeIdList, sDate, eDate, companyTypeSyn), threadPoolExecutor);
            futures.add(future);
        }
        //等待所有线程执行完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        long t2 = System.currentTimeMillis();
        logger.info("批量处理多线程结束,总耗时:{}ms", t2 - t1);
        //获取线程返回结果,封装集合
        List<OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo> results = new ArrayList<>();
        futures.stream().forEach(future -> {
            try {
                List<OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo> list = (List<OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo>) future.get();
                if (CollectionUtils.isNotEmpty(list)) {
                    results.addAll(list);
                }
            } catch (Exception e) {
                logger.error("获取多线程返回结果数据异常", e);
            }
        });

        if (CollectionUtils.isNotEmpty(results)) {
            /*********************** 处理请假数据开始 ***********************/
            List<DtLeave> needSaveDtLeaves = new ArrayList<>();
            //处理请假数据
            needSaveDtLeaves.addAll(hanldLeaves(startDateTime, endDateTime, companyType, idAndName, results));
            if (CollectionUtils.isNotEmpty(needSaveDtLeaves)) {
                //清除数据
                leaveService.deleteByBatchNoRange(startDateTimeBatchNo, endDateTimeBatchNo, employeeIds, companyType);
                //数据入库
                leaveService.batchInsert(needSaveDtLeaves);
                //考勤汇总
                iSynAttendanceAnalyzeService.synAttendanceAnalyze(DateUtils.parseDateToStr("yyyy-MM-dd", startDateTime), DateUtils.parseDateToStr("yyyy-MM-dd", endDateTime));
            }
            /*********************** 处理请假数据结束 ***********************/

        }
    }

    private List<OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo> getUpdatedata(List<String> employeeIds, Date sDate, Date eDate, Integer companyType) {
        Date curStartDate = sDate;
        Date curEndDate = eDate;
        List<OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo> results = new ArrayList<>();
        while (curStartDate.compareTo(curEndDate) <= 0) {
            for (String dtEmployeeId : employeeIds) {
                results.add(dingTalkService.getUpdatedata(dtEmployeeId, curStartDate, companyType));
            }
            Calendar instance = Calendar.getInstance();
            instance.setTime(curStartDate);
            instance.add(Calendar.DATE, 1);
            curStartDate = instance.getTime();
        }
        return results;
    }


    private List<DtLeave> hanldLeaves(Date startDateTime, Date endDateTime, Integer companyType, Map<String, String> idAndName, List<OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo> results) {
        //需要保存的请假记录
        List<DtLeave> needSaves = new ArrayList<>();
        //已处理工单
        List<String> handledProcInstIds = new ArrayList<>();
        for (OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo result : results) {
            //获取审批单
            List<OapiAttendanceGetupdatedataResponse.AtApproveForOpenVo> approveList = result.getApproveList();
            String userId = result.getUserid();
            String userName = idAndName.get(userId);
            String endDateTimeBatchNo = DateUtils.parseDateToStr("yyyyMMdd", endDateTime);
            String startDateTimeBatchNo = DateUtils.parseDateToStr("yyyyMMdd", startDateTime);
            if (CollectionUtils.isNotEmpty(approveList)) {
                //获取审批单中的请假单
                List<OapiAttendanceGetupdatedataResponse.AtApproveForOpenVo> leaves = approveList.stream().filter(item -> item.getBizType().equals(3L)).collect(Collectors.toList());
                for (OapiAttendanceGetupdatedataResponse.AtApproveForOpenVo leave : leaves) {
                    String procInstId = leave.getProcInstId();
                    if (handledProcInstIds.contains(procInstId)) {
                        continue;
                    } else {
                        handledProcInstIds.add(procInstId);
                    }
                    //开始时间
                    Calendar sInstance = Calendar.getInstance();
                    sInstance.setTime(leave.getBeginTime());
                    LocalDate sDate = LocalDate.of(sInstance.get(Calendar.YEAR), sInstance.get(Calendar.MONTH) + 1 , sInstance.get(Calendar.DAY_OF_MONTH));
                    //结束时间
                    Calendar eInstance = Calendar.getInstance();
                    eInstance.setTime(leave.getEndTime());
                    LocalDate eDate = LocalDate.of(eInstance.get(Calendar.YEAR), eInstance.get(Calendar.MONTH) + 1 , eInstance.get(Calendar.DAY_OF_MONTH));
                    List<String> datesInRange = generateDatesInRange(sDate, eDate);
                    DtLeave baseLeave = new DtLeave();
                    baseLeave.setEmployeeId(userId);
                    baseLeave.setName(userName);
                    baseLeave.setDurationUnit(leave.getDurationUnit());
                    //审批单实例ID
                    baseLeave.setLeaveNo(procInstId);
                    baseLeave.setDelFlag("0");
                    baseLeave.setCompanyType(companyType);
                    baseLeave.setSubType(leave.getSubType());
                    baseLeave.setTagName(leave.getTagName());
                    baseLeave.setBizType(leave.getBizType());
                    for (String curDate : datesInRange) {
                        DtLeave cur = new DtLeave();
                        BeanUtils.copyProperties(baseLeave, cur);
                        //判断是否和开始时间为同一天
                        Date beginTime = leave.getBeginTime();
                        String s = DateUtils.parseDateToStr("yyyy-MM-dd", beginTime);
                        //与开始时间为同一天
                        Date constructBeginDate = null;
                        if (StringUtils.equals(curDate, s)) {
                            constructBeginDate = covertDate("s", beginTime);
                        } else { //与开始时间不为同一天
                            Date date = DateUtils.dateTime("yyyy-MM-dd", curDate);
                            Calendar instance = Calendar.getInstance();
                            instance.setTime(date);
                            instance.set(Calendar.HOUR_OF_DAY, 9);
                            instance.set(Calendar.MINUTE, 30);
                            instance.set(Calendar.SECOND, 00);
                            constructBeginDate = instance.getTime();
                        }

                        //判断是否和结束时间为同一天
                        Date endDate = leave.getEndTime();
                        String e = DateUtils.parseDateToStr("yyyy-MM-dd", endDate);
                        //如果与结束时间为同一天,则构造结束时间
                        Date constructEndDate = null;
                        if (StringUtils.equals(curDate, e)) {
                            constructEndDate = covertDate("e", endDate);
                        } else { //如果与结束时间不为同一天
                            Date date = DateUtils.dateTime("yyyy-MM-dd", curDate);
                            Calendar instance = Calendar.getInstance();
                            instance.setTime(date);
                            instance.set(Calendar.HOUR_OF_DAY, 18);
                            instance.set(Calendar.MINUTE, 30);
                            instance.set(Calendar.SECOND, 00);
                            constructEndDate = instance.getTime();
                        }
                        cur.setStartTime(constructBeginDate);
                        cur.setStartTimeStamp(String.valueOf(constructBeginDate.getTime()));
                        cur.setEndTime(constructEndDate);
                        cur.setEndTimeStamp(String.valueOf(constructEndDate.getTime()));
                        cur.setDataId(userId.concat(cur.getStartTimeStamp()).concat(cur.getEndTimeStamp()).concat(cur.getCompanyType().toString()));
                        cur.setBatchNo(DateUtils.getBatchNo(Long.valueOf(cur.getEndTimeStamp())));
                        if (cur.getDurationUnit().equals("DAY")){
                            //构造下午一点
                            Calendar afternoon_1 = Calendar.getInstance();
                            afternoon_1.setTime(cur.getEndTime());
                            afternoon_1.set(Calendar.HOUR_OF_DAY, 13);
                            afternoon_1.set(Calendar.MINUTE, 00);
                            afternoon_1.set(Calendar.SECOND, 00);
                            if(cur.getStartTime().compareTo(afternoon_1.getTime()) >=0 || cur.getEndTime().compareTo(afternoon_1.getTime()) <=0){
                                cur.setDuration("0.5");
                            }else{
                                cur.setDuration("1.0");
                            }
                        }else{
                            cur.setDuration(leave.getDuration());
                        }
                        if (Integer.valueOf(cur.getBatchNo()) <= Integer.valueOf(endDateTimeBatchNo) && Integer.valueOf(cur.getBatchNo()) >= Integer.valueOf(startDateTimeBatchNo)) {
                            cur.setCreateBy("1");
                            needSaves.add(cur);
                        }
                    }
                }
            }
        }

        return needSaves;
    }

    public List<String> generateDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 使用LocalDate.until()方法获取日期范围，并遍历每一天
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date.format(formatter));
        }

        return dates;
    }


    /**
     * @description: 根据时间转换为上午或下午
     * @author: zhaobo.yang
     * @date: 2023/10/9 17:00
     */
    private Long covertDate(String flag, Long timestamp) {
        Date currentTime = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("a");
        String format = formatter.format(currentTime);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timestamp);
        if ("上午".equals(format) && flag.equals("s")) { //开始时间、上午
            instance.set(Calendar.HOUR_OF_DAY, 9);
            instance.set(Calendar.MINUTE, 30);
            instance.set(Calendar.SECOND, 00);
        } else if ("上午".equals(format) && flag.equals("e")) { //结束时间、上午
            instance.set(Calendar.HOUR_OF_DAY, 12);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
        } else if ("下午".equals(format) && flag.equals("s")) { //开始时间、下午
            instance.set(Calendar.HOUR_OF_DAY, 13);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
        } else if ("下午".equals(format) && flag.equals("e")) { //结束时间、下午
            instance.set(Calendar.HOUR_OF_DAY, 18);
            instance.set(Calendar.MINUTE, 30);
            instance.set(Calendar.SECOND, 00);
        }
        return instance.getTimeInMillis();
    }

    /**
     * @description: 根据时间转换为上午或下午
     * @author: zhaobo.yang
     * @date: 2023/10/9 17:00
     */
    private Date covertDate(String flag, Date currentTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("a");
        String format = formatter.format(currentTime);
        Calendar instance = Calendar.getInstance();
        instance.setTime(currentTime);
        if ("上午".equals(format) && flag.equals("s")) { //开始时间、上午
            instance.set(Calendar.HOUR_OF_DAY, 9);
            instance.set(Calendar.MINUTE, 30);
            instance.set(Calendar.SECOND, 00);
        } else if ("上午".equals(format) && flag.equals("e")) { //结束时间、上午
            instance.set(Calendar.HOUR_OF_DAY, 12);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
        } else if ("下午".equals(format) && flag.equals("s")) { //开始时间、下午
            instance.set(Calendar.HOUR_OF_DAY, 13);
            instance.set(Calendar.MINUTE, 00);
            instance.set(Calendar.SECOND, 00);
        } else if ("下午".equals(format) && flag.equals("e")) { //结束时间、下午
            //构造下午一点的时间
            Calendar afternoon_1 = Calendar.getInstance();
            afternoon_1.setTime(currentTime);
            afternoon_1.set(Calendar.HOUR_OF_DAY, 13);
            afternoon_1.set(Calendar.MINUTE, 00);
            afternoon_1.set(Calendar.SECOND, 00);
            //如果在一点之前，则构造为上午12点
            if (currentTime.compareTo(afternoon_1.getTime()) <=0){
                instance.set(Calendar.HOUR_OF_DAY, 12);
                instance.set(Calendar.MINUTE, 00);
                instance.set(Calendar.SECOND, 00);
            }else{
                instance.set(Calendar.HOUR_OF_DAY, 18);
                instance.set(Calendar.MINUTE, 30);
                instance.set(Calendar.SECOND, 00);
            }
        }
        return instance.getTime();
    }
}
