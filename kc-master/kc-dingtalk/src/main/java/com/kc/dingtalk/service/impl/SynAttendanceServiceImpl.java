package com.kc.dingtalk.service.impl;

import com.dingtalk.api.response.OapiAttendanceListRecordResponse;
import com.dingtalk.api.response.OapiCheckinRecordResponse;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtAttendance;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.core.redis.RedisCache;
import com.kc.common.enums.AttendanceDataType;
import com.kc.common.enums.AttendanceType;
import com.kc.common.enums.YesOrNo;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.DingTalkService;
import com.kc.dingtalk.mapper.DtAttendanceMapper;
import com.kc.dingtalk.mapper.DtEmployeeMapper;
import com.kc.dingtalk.service.ISynAttendanceService;
import com.kc.system.mapper.SysDeptMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 同步钉钉员工基础信息Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
@Service
public class SynAttendanceServiceImpl implements ISynAttendanceService {
    private static Logger log = LoggerFactory.getLogger(SynAttendanceServiceImpl.class);

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DtAttendanceMapper attendanceMapper;
    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private DtEmployeeMapper employeeMapper;

    private String attendanceEmployeeNameKey = "com.kc.dingtalk.service.impl.SynAttendanceServiceImpl:attendance:%s";

    /**
     * @param sDate 开始日期(yyyy-MM-dd)
     * @param eDate   结束时间(yyyy-MM-dd)
     * @description:
     * @author: zhaobo.yang
     * @date: 2022/11/7 15:47
     */
    @Override
    public void synAttendance(Date sDate, Date eDate,int companyType) {
        //查询所有用户id
        EmployeeDao query = new EmployeeDao();
        query.setCompanyType(companyType);
        query.setCanDimission(YesOrNo.否_0.getCode()); //查询未离职的
        List<DtEmployee> employees = employeeMapper.selectDtEmployeeList(query);
        if (CollectionUtils.isEmpty(employees)) {
            return;
        }
        //设置员工employeeId和name
        List<String> userIds = new ArrayList<>();
        for (DtEmployee e : employees) {
            redisCache.setCacheObject(String.format(attendanceEmployeeNameKey, e.getEmployeeId()), e.getName(), 60, TimeUnit.MINUTES);
            if (!userIds.contains(e.getEmployeeId())) {
                userIds.add(e.getEmployeeId());
            }
        }
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<Date> dates = new ArrayList<>();
        while (!sDate.after(eDate)) {
            dates.add(sDate);
            Calendar instance = Calendar.getInstance();
            instance.setTime(sDate);
            instance.add(Calendar.DATE, 1);
            sDate = instance.getTime();
        }
        //人员按照五十个切分
        List<List<String>> partition = ListUtils.partition(userIds, 50);
        //日期按照八天切分
        List<List<Date>> datePartition = ListUtils.partition(dates, 8);
        //同步人员考勤数据
        List<OapiAttendanceListRecordResponse.Recordresult> attendances = new ArrayList<>();
        for (List<Date> dateList : datePartition) {
            String paramDay1 = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dateList.get(0));
            String paramDay2 = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dateList.get(dateList.size() - 1));
            for (List<String> uIds : partition) {
                //同步打卡数据
                List<OapiAttendanceListRecordResponse.Recordresult> attendance = dingTalkService.getAttendances(uIds, paramDay1, paramDay2,companyType);
                if (CollectionUtils.isNotEmpty(attendance)){
                    attendances.addAll(attendance);
                }
            }
        }
        if (CollectionUtils.isEmpty(attendances)){
            return;
        }
        List<DtAttendance> saves = new ArrayList<>();
        for (OapiAttendanceListRecordResponse.Recordresult item : attendances) {
            DtAttendance dtAttendance = new DtAttendance();
            dtAttendance.setType(AttendanceType.打卡记录_0.getCode()); //代表钉钉打卡记录;
            dtAttendance.setEmployeeId(item.getUserId());
            String userName = redisCache.getCacheObject(String.format(attendanceEmployeeNameKey, item.getUserId()));
            dtAttendance.setName(userName);
            Long timeStamp = item.getUserCheckTime().getTime();
            dtAttendance.setBatchNo(DateUtils.getBatchNo(timeStamp));
            dtAttendance.setUserCheckTimeStamp(timeStamp.toString());
            dtAttendance.setCompanyType(companyType);
            dtAttendance.setDataType(AttendanceDataType.钉钉同步_0.getCode());
            dtAttendance.setPlace(StringUtils.isBlank(item.getUserAddress())?"未知":item.getUserAddress());//打卡地区
            dtAttendance.setDelFlag(YesOrNo.否_0.getCode());
            //时间戳转换成时间
            Date userCheckTime = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", item.getUserCheckTime()));
            dtAttendance.setUserCheckTime(userCheckTime);
            String dataId = dtAttendance.getEmployeeId() + dtAttendance.getUserCheckTimeStamp() + dtAttendance.getType();
            dtAttendance.setDataId(dataId);
            dtAttendance.setCreateBy("1");//管理员
            dtAttendance.setCreateTime(DateUtils.getYYYYMMDDHHSSMM());
            dtAttendance.setUpdateBy("1");//管理员
            dtAttendance.setUpdateTime(DateUtils.getYYYYMMDDHHSSMM());
            saves.add(dtAttendance);
        }
        if(CollectionUtils.isNotEmpty(saves)){
            //批量入库
            attendanceMapper.batchInsertDtAttendance(saves);
        }
    }

    /**
     * @description: 根据部门拉去员工签到数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/25 14:54
     */
    @Override
    public void synSignIn(Date sDate, Date eDate, int companyType) {

        List<Date> dates = new ArrayList<>();
        while (!sDate.after(eDate)) {
            dates.add(sDate);
            Calendar instance = Calendar.getInstance();
            instance.setTime(sDate);
            instance.add(Calendar.DATE, 1);
            sDate = instance.getTime();
        }
        List<List<Date>> datePartition = ListUtils.partition(dates, 45);
        List<Long> deptIds = new ArrayList<>();
        //根据部门拉取用户签到信息
        SysDept query = new SysDept();
        query.setCompanyType(companyType);
        List<SysDept> sysDepts = sysDeptMapper.selectDeptList(query);
        deptIds = sysDepts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (List<Date> dateList : datePartition) {
            String paramDay1 = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dateList.get(0));
            String paramDay2 = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, dateList.get(dateList.size() - 1));
            List<OapiCheckinRecordResponse.Data> signInRecordsByDeptIds = new ArrayList<>();
            for (Long deptId : deptIds) {
                List<OapiCheckinRecordResponse.Data> signInRecordsByDeptId = dingTalkService.getSignInRecordsByDeptId(deptId.toString(), 0L, 100L, paramDay1, paramDay2, companyType);
                if (CollectionUtils.isNotEmpty(signInRecordsByDeptId)) {
                    signInRecordsByDeptIds.addAll(signInRecordsByDeptId);
                }
            }
            List<DtAttendance> saves = new ArrayList<>();
            for (OapiCheckinRecordResponse.Data data : signInRecordsByDeptIds) {
                DtAttendance dtAttendance = new DtAttendance();
                dtAttendance.setType(AttendanceType.签到记录_1.getCode()); //代表钉钉签到记录;
                dtAttendance.setEmployeeId(data.getUserId());
                dtAttendance.setName(data.getName());
                dtAttendance.setPlace(StringUtils.isBlank(data.getPlace())?"未知":data.getPlace());
                //打卡时间戳(毫秒值)
                Long checkinTimeTimeStamp = data.getTimestamp();
                dtAttendance.setUserCheckTimeStamp(checkinTimeTimeStamp.toString());
                dtAttendance.setBatchNo(DateUtils.getBatchNo(checkinTimeTimeStamp));
                dtAttendance.setCompanyType(companyType);
                dtAttendance.setDataType(AttendanceDataType.钉钉同步_0.getCode());
                dtAttendance.setDelFlag(YesOrNo.否_0.getCode());
                Date userCheckTime = new Date(checkinTimeTimeStamp);
                userCheckTime = DateUtils.dateTime( "yyyy-MM-dd HH:mm:ss",DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", userCheckTime));
                dtAttendance.setUserCheckTime(userCheckTime);
                String dataId = dtAttendance.getEmployeeId() + dtAttendance.getUserCheckTimeStamp() + dtAttendance.getType();
                dtAttendance.setDataId(dataId);
                dtAttendance.setCreateBy("1");//管理员
                dtAttendance.setCreateTime(DateUtils.getYYYYMMDDHHSSMM());
                dtAttendance.setUpdateBy("1");//管理员
                dtAttendance.setUpdateTime(DateUtils.getYYYYMMDDHHSSMM());
                saves.add(dtAttendance);
            }
            if(CollectionUtils.isNotEmpty(saves)){
                //批量入库
                attendanceMapper.batchInsertDtAttendance(saves);
            }
        }
    }
}
