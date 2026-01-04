package com.kc.dingtalk.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.DtTravel;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.HuiLianYiService;
import com.kc.dingtalk.service.IDtEmployeeService;
import com.kc.dingtalk.service.ISynAttendanceAnalyzeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kc.dingtalk.mapper.DtTravelMapper;
import com.kc.dingtalk.service.IDtTravelService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * 外出记录Service业务层处理
 *
 * @author kc
 * @date 2024-05-06
 */
@Service
public class DtTravelServiceImpl implements IDtTravelService
{
    @Autowired
    private DtTravelMapper dtTravelMapper;

    @Autowired
    private HuiLianYiService huiLianYiService;

    @Autowired
    private IDtEmployeeService employeeService;

    @Autowired
    private ISynAttendanceAnalyzeService iSynAttendanceAnalyzeService;


    /**
     * 查询外出记录
     *
     * @param id 外出记录主键
     * @return 外出记录
     */
    @Override
    public DtTravel selectDtTravelById(Long id)
    {
        return dtTravelMapper.selectDtTravelById(id);
    }

    /**
     * 查询外出记录列表
     *
     * @param dtTravel 外出记录
     * @return 外出记录
     */
    @Override
    public List<DtTravel> selectDtTravelList(DtTravel dtTravel)
    {
        return dtTravelMapper.selectDtTravelList(dtTravel);
    }

    /**
     * 新增外出记录
     *
     * @param dtTravel 外出记录
     * @return 结果
     */
    @Override
    public int insertDtTravel(DtTravel dtTravel)
    {
        dtTravel.setCreateTime(DateUtils.getNowDate());
        return dtTravelMapper.insertDtTravel(dtTravel);
    }

    /**
     * 修改外出记录
     *
     * @param dtTravel 外出记录
     * @return 结果
     */
    @Override
    public int updateDtTravel(DtTravel dtTravel)
    {
        dtTravel.setUpdateTime(DateUtils.getNowDate());
        return dtTravelMapper.updateDtTravel(dtTravel);
    }

    /**
     * 批量删除外出记录
     *
     * @param ids 需要删除的外出记录主键
     * @return 结果
     */
    @Override
    public int deleteDtTravelByIds(Long[] ids)
    {
        return dtTravelMapper.deleteDtTravelByIds(ids);
    }

    /**
     * 删除外出记录信息
     *
     * @param id 外出记录主键
     * @return 结果
     */
    @Override
    public int deleteDtTravelById(Long id)
    {
        return dtTravelMapper.deleteDtTravelById(id);
    }

    /**
     * 批量插入
     * @param dtTravels
     * @return
     */
    public int batchInsert(List<DtTravel> dtTravels)
    {
        return dtTravelMapper.batchInsert(dtTravels);
    }

    public void synTravelByHuiLianYi(Date startDate, Date endDate)
    {
        //获取汇联易差旅申请单
        JSONArray travelsJsonArray = huiLianYiService.getTravelsFromHuiLianYi(
                DateUtils.parseDateToStr("yyyy-MM-dd", startDate) + " 00:00:00",
                DateUtils.parseDateToStr("yyyy-MM-dd", endDate) + " 23:59:59"
        );
        if (CollectionUtils.isNotEmpty(travelsJsonArray)){
            //查询本地人员信息
            List<DtEmployee> employees = employeeService.selectDtEmployeeListNoAuth(new EmployeeDao());
            if (CollectionUtils.isEmpty(employees)) {
                return;
            }
            Map<String, String> jobNumAndId = employees.stream().collect(Collectors.toMap(DtEmployee::getJobNumber, DtEmployee::getEmployeeId, (value1, value2) -> value1));
            Map<String, String> idAndName = employees.stream().collect(Collectors.toMap(DtEmployee::getEmployeeId, DtEmployee::getName, (value1, value2) -> value1));

            List<DtTravel> needSaves = new ArrayList<>();
            List<String> businessCodes = new ArrayList<>();
            for (Object o : travelsJsonArray) {
                JSONObject item = JSONObject.parseObject(o.toString());
                JSONArray simpleApplicationParticipants = item.getJSONArray("simpleApplicationParticipants");
                if (CollectionUtils.isEmpty(simpleApplicationParticipants)){
                    continue;
                }
                String formName = item.getString("formName");
                String version = item.getString("version");
                String businessCode = item.getString("businessCode");
                businessCodes.add(businessCode);
                String status = item.getString("status");
                String rejectType = item.getString("rejectType");
                JSONObject travelApplication = item.getJSONObject("travelApplication");
                Date sDate = travelApplication.getDate("startDate");
                Date eDate = travelApplication.getDate("endDate");
                //构造外出记录
                //根据工号查询人员id和人员姓名
                DtTravel baseTravel = new DtTravel();
                baseTravel.setFormName(formName);
                baseTravel.setVersion(version);
                baseTravel.setBusinessCode(businessCode);
                baseTravel.setOrderStatus(status);
                baseTravel.setRejectType(rejectType);
                baseTravel.setCreateBy("1");
                baseTravel.setStartTime(convertDateToLocalDate(sDate));
                baseTravel.setEndTime(convertDateToLocalDate(eDate));
                baseTravel.setOrderStartTime(convertDateToLocalDate(sDate));
                baseTravel.setOrderEndTime(convertDateToLocalDate(eDate));
                baseTravel.setDelFlag("0");
                //根据开始时间结束时间构造每一天的外出记录
                //开始时间
                Calendar sInstance = Calendar.getInstance();
                sInstance.setTime(baseTravel.getStartTime());
                LocalDate sLocalDate = LocalDate.of(sInstance.get(Calendar.YEAR), sInstance.get(Calendar.MONTH) + 1 , sInstance.get(Calendar.DAY_OF_MONTH));
                //结束时间
                Calendar eInstance = Calendar.getInstance();
                eInstance.setTime(baseTravel.getEndTime());
                LocalDate eLocalDate = LocalDate.of(eInstance.get(Calendar.YEAR), eInstance.get(Calendar.MONTH) + 1 , eInstance.get(Calendar.DAY_OF_MONTH));
                List<String> datesInRange = generateDatesInRange(sLocalDate, eLocalDate);
                for (String curDate : datesInRange) {
                    DtTravel cur = new DtTravel();
                    BeanUtils.copyProperties(baseTravel, cur);
                    //判断是否和开始时间为同一天
                    Date beginTime = baseTravel.getStartTime();
                    String s = DateUtils.parseDateToStr("yyyy-MM-dd", beginTime);
                    //与开始时间为同一天
                    Date constructBeginDate = null;
                    if (StringUtils.equals(curDate, s)) {
                        constructBeginDate = beginTime;
                    } else { //与开始时间不为同一天
                        Date date = DateUtils.dateTime("yyyy-MM-dd", curDate);
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(date);
                        instance.set(Calendar.HOUR_OF_DAY, 00);
                        instance.set(Calendar.MINUTE, 00);
                        instance.set(Calendar.SECOND, 00);
                        constructBeginDate = instance.getTime();
                    }
                    //判断是否和结束时间为同一天
                    Date endTime = baseTravel.getEndTime();
                    String e = DateUtils.parseDateToStr("yyyy-MM-dd", endTime);
                    //如果与结束时间为同一天,则构造结束时间
                    Date constructEndDate = null;
                    if (StringUtils.equals(curDate, e)) {
                        constructEndDate = endTime;
                    } else { //如果与结束时间不为同一天
                        Date date = DateUtils.dateTime("yyyy-MM-dd", curDate);
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(date);
                        instance.set(Calendar.HOUR_OF_DAY, 23);
                        instance.set(Calendar.MINUTE, 59);
                        instance.set(Calendar.SECOND, 59);
                        constructEndDate = instance.getTime();
                    }
                    cur.setStartTime(constructBeginDate);
                    cur.setEndTime(constructEndDate);
                    cur.setBatchNo(DateUtils.getBatchNo(Long.valueOf(constructEndDate.getTime())));
                    //设置时长
                    long l = cur.getEndTime().getTime() - cur.getStartTime().getTime();
                    BigDecimal diff = BigDecimal.valueOf(l);
                    BigDecimal divisor = BigDecimal.valueOf(1000 * 60 * 60);
                    BigDecimal result = diff.divide(divisor, 2, RoundingMode.HALF_UP);
                    cur.setDuration(result.toString());
                    Integer endDateTimeBatchNo = Integer.valueOf(DateUtils.parseDateToStr("yyyyMMdd", endDate));
                    Integer startDateTimeBatchNo = Integer.valueOf(DateUtils.parseDateToStr("yyyyMMdd",startDate ));
                    if (Integer.valueOf(cur.getBatchNo()) <= endDateTimeBatchNo && Integer.valueOf(cur.getBatchNo()) >= startDateTimeBatchNo) {
                        cur.setCreateBy("1");
                        //根据同行人构造入库记录
                        for (Object simpleApplicationParticipant : simpleApplicationParticipants) {
                             //转化为JSONObject
                            JSONObject simpleApplicationParticipantJSONObject = JSONObject.parseObject(simpleApplicationParticipant.toString());
                            String jobNumber = simpleApplicationParticipantJSONObject.getString("employeeID");
                            String employeeId = jobNumAndId.get(jobNumber);
                            DtTravel save = new DtTravel();
                            if (StringUtils.isNotEmpty(employeeId)) {
                                BeanUtils.copyProperties(cur, save);
                                save.setEmployeeId(employeeId);
                                save.setName(idAndName.get(employeeId));
                            }else {
                                continue;
                            }
                            needSaves.add(save);
                        }
                    }
                }
            }
            //根据businessCode删除已存在的数据
            if(CollectionUtils.isNotEmpty(businessCodes)){
                dtTravelMapper.deleteByBusinessCodes(businessCodes);
            }
            //批量新增数据
            if(CollectionUtils.isNotEmpty(needSaves)){
                this.batchInsert(needSaves);
            }
            //汇总
            iSynAttendanceAnalyzeService.synAttendanceAnalyze(DateUtils.parseDateToStr("yyyy-MM-dd", startDate),
                    DateUtils.parseDateToStr("yyyy-MM-dd", endDate));
        }
    }


    /**
     * 将传入的日期字符串转换为本地日期格式。
     *
     * @param date 代表日期的字符串。预期为符合特定格式的字符串。
     * @return 转换后的本地日期对象。如果转换失败，可能返回null。
     */
    private Date convertDateToLocalDate(Date date) {
        // 创建一个SimpleDateFormat对象，用于格式化日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置时区为东八区（北京时间）
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 使用东八区的时区格式化世界时间
        String beijingTime = formatter.format(date);
        return DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", beijingTime);
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

}
