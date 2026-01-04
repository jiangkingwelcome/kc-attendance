package com.kc.dingtalk.service.impl;

import com.kc.common.annotation.DataScope;
import com.kc.common.core.domain.dao.AttendanceAnalyzeDao;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtAttendanceAnalyze;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.mapper.DtAttendanceAnalyzeMapper;
import com.kc.dingtalk.service.IDtAttendanceAnalyzeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工考勤汇总分析Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2022-11-08
 */
@Service
public class DtAttendanceAnalyzeServiceImpl implements IDtAttendanceAnalyzeService
{
    @Autowired
    private DtAttendanceAnalyzeMapper dtAttendanceAnalyzeMapper;

    @Autowired
    private DtEmployeeServiceImpl dtEmployeeMapper;

    /**
     * 查询员工考勤汇总分析
     *
     * @param id 员工考勤汇总分析主键
     * @return 员工考勤汇总分析
     */
    @Override
    public DtAttendanceAnalyze selectDtAttendanceAnalyzeById(Long id)
    {
        return dtAttendanceAnalyzeMapper.selectDtAttendanceAnalyzeById(id);
    }

    /**
     * 查询员工考勤汇总分析列表
     *
     * @param attendanceAnalyzeDao 员工考勤汇总分析
     * @return 员工考勤汇总分析
     */
    @Override
    @DataScope(userAlias = "ue",deptAlias = "d")
    public List<DtAttendanceAnalyze> selectDtAttendanceAnalyzeList(AttendanceAnalyzeDao attendanceAnalyzeDao)
    {
        List<DtAttendanceAnalyze> dtAttendanceAnalyzes = dtAttendanceAnalyzeMapper.selectDtAttendanceAnalyzeList(attendanceAnalyzeDao);
        //查询所有员工
        EmployeeDao queryDao = new EmployeeDao();
        List<DtEmployee> employees = dtEmployeeMapper.selectDtEmployeeList(queryDao);
        Map<String, Date> hiredDateMap = new HashMap<>(); // 入职时间Map
        Map<String, Date> lastWorkDayMap = new HashMap<>(); // 离职时间Map
        for (DtEmployee employee : employees) {
            String employeeId = employee.getEmployeeId();
            if (employeeId == null || employeeId.trim().isEmpty()) {
                continue; //
            }

            Date hiredDate = employee.getHiredDate();
            if (hiredDate != null ) {
                hiredDateMap.put(employeeId, hiredDate);
            }

            Date lastWorkDay = employee.getLastWorkDay();
            if (lastWorkDay != null ) {
                lastWorkDayMap.put(employeeId, lastWorkDay);
            }
        }

        if (CollectionUtils.isNotEmpty(dtAttendanceAnalyzes)){
            for (DtAttendanceAnalyze dtAttendanceAnalyze : dtAttendanceAnalyzes) {
                String employeeId = dtAttendanceAnalyze.getEmployeeId();
                if (hiredDateMap.containsKey(employeeId)){
                    Date hiredDate = hiredDateMap.get(employeeId);
                    dtAttendanceAnalyze.setHiredDate(hiredDate);
                }
                if (lastWorkDayMap.containsKey(employeeId)){
                    Date lastWorkDay = lastWorkDayMap.get(employeeId);
                    dtAttendanceAnalyze.setLastWorkDay(lastWorkDay);
                }

                String leaveRecord = dtAttendanceAnalyze.getLeaveRecord();
                String travelRecord = dtAttendanceAnalyze.getTravelRecord();
                if (StringUtils.isNotBlank(leaveRecord) && leaveRecord.contains("|")){
                    String[] split = leaveRecord.split("\\|");
                    leaveRecord = "";
                    for (int i = 0; i < split.length; i++) {
                        String s = split[i];
                        leaveRecord+=s.concat("<br/>");
                    }
                    dtAttendanceAnalyze.setLeaveRecord(leaveRecord);
                }
                if (StringUtils.isNotBlank(travelRecord) && travelRecord.contains("|")){
                    String[] split = travelRecord.split("\\|");
                    travelRecord = "";
                    for (int i = 0; i < split.length; i++) {
                        String s = split[i];
                        travelRecord+=s.concat("<br/>");
                    }
                    dtAttendanceAnalyze.setTravelRecord(travelRecord);
                }
            }
        }
        return dtAttendanceAnalyzes;
    }

    /**
     * 新增员工考勤汇总分析
     *
     * @param dtAttendanceAnalyze 员工考勤汇总分析
     * @return 结果
     */
    @Override
    public int insertDtAttendanceAnalyze(DtAttendanceAnalyze dtAttendanceAnalyze)
    {
        dtAttendanceAnalyze.setCreateTime(DateUtils.getNowDate());
        return dtAttendanceAnalyzeMapper.insertDtAttendanceAnalyze(dtAttendanceAnalyze);
    }

    /**
     * 修改员工考勤汇总分析
     *
     * @param dtAttendanceAnalyze 员工考勤汇总分析
     * @return 结果
     */
    @Override
    public int updateDtAttendanceAnalyze(DtAttendanceAnalyze dtAttendanceAnalyze)
    {
        dtAttendanceAnalyze.setUpdateTime(DateUtils.getNowDate());
        return dtAttendanceAnalyzeMapper.updateDtAttendanceAnalyze(dtAttendanceAnalyze);
    }

    /**
     * 批量删除员工考勤汇总分析
     *
     * @param ids 需要删除的员工考勤汇总分析主键
     * @return 结果
     */
    @Override
    public int deleteDtAttendanceAnalyzeByIds(Long[] ids)
    {
        return dtAttendanceAnalyzeMapper.deleteDtAttendanceAnalyzeByIds(ids);
    }

    /**
     * 删除员工考勤汇总分析信息
     *
     * @param id 员工考勤汇总分析主键
     * @return 结果
     */
    @Override
    public int deleteDtAttendanceAnalyzeById(Long id)
    {
        return dtAttendanceAnalyzeMapper.deleteDtAttendanceAnalyzeById(id);
    }

    @Override
    public void deleteAttendanceAnalyze(String batchNo) {
        dtAttendanceAnalyzeMapper.deleteByBatchNoRange("00000000",batchNo);
    }
}
