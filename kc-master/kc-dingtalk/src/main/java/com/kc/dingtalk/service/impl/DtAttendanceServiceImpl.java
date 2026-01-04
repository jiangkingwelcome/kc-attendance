package com.kc.dingtalk.service.impl;

import com.kc.common.annotation.DataScope;
import com.kc.common.core.domain.dao.AttendanceDao;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtAttendance;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.enums.YesOrNo;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.mapper.DtAttendanceMapper;
import com.kc.dingtalk.service.IDtAttendanceService;
import com.kc.dingtalk.service.IDtEmployeeService;
import com.kc.dingtalk.service.ISynAttendanceAnalyzeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 * 员工考勤记录Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2022-11-07
 */
@Service
public class DtAttendanceServiceImpl implements IDtAttendanceService
{
    @Autowired
    private DtAttendanceMapper dtAttendanceMapper;
    @Autowired
    private ISynAttendanceAnalyzeService synAttendanceAnalyzeService;
    @Autowired
    private IDtEmployeeService employeeService;
    /**
     * 查询员工考勤记录
     *
     * @param id 员工考勤记录主键
     * @return 员工考勤记录
     */
    @Override
    public DtAttendance selectDtAttendanceById(Long id)
    {
        return dtAttendanceMapper.selectDtAttendanceById(id);
    }

    /**
     * 查询员工考勤记录列表
     *
     * @param query 员工考勤记录
     * @return 员工考勤记录
     */
    @Override
    @DataScope(userAlias = "ue",deptAlias = "d")
    public List<DtAttendance> selectDtAttendanceList(AttendanceDao query)
    {
        return dtAttendanceMapper.selectDtAttendanceList(query);
    }

    /**
     * 新增员工考勤记录
     *
     * @param dtAttendance 员工考勤记录
     * @return 结果
     */
    @Override
    public int insertDtAttendance(DtAttendance dtAttendance)
    {
        long time = dtAttendance.getUserCheckTime().getTime();
        dtAttendance.setUserCheckTimeStamp(String.valueOf(time));
        //数据校验
        String dataId = dtAttendance.getEmployeeId()+dtAttendance.getUserCheckTimeStamp()+dtAttendance.getType();
        if (StringUtils.isBlank(dataId)){
            throw new RuntimeException("唯一标识不可为空!");
        }
        AttendanceDao dao = new AttendanceDao();
        dao.setDataId(dataId);
        List<DtAttendance> attendanceList = dtAttendanceMapper.selectDtAttendanceList(dao);
        int size = attendanceList.size();
        if (size == 1){
           throw new RuntimeException("数据已存在,不可新增！");
        }
        String userId = dtAttendance.getEmployeeId();
        String name = dtAttendance.getName();
        EmployeeDao dtEmployee = new EmployeeDao();
        dtEmployee.setEmployeeId(userId);
        dtEmployee.setName(name);
        List<DtEmployee> dtEmployees = employeeService.selectDtEmployeeList(dtEmployee);
        if (CollectionUtils.isEmpty(dtEmployees)){
            throw new RuntimeException("员工名称有误！");
        }
        //整合入库数据
        dtAttendance.setDataId(dataId);
        dtAttendance.setDelFlag(YesOrNo.否_0.getCode());
        dtAttendance.setBatchNo(DateUtils.getBatchNo(time));
        dtAttendance.setCreateBy("1");
        dtAttendance.setCreateTime(DateUtils.getYYYYMMDDHHSSMM());
        dtAttendance.setUpdateBy("1");
        dtAttendance.setUpdateTime(DateUtils.getYYYYMMDDHHSSMM());
        dtAttendanceMapper.insertDtAttendance(dtAttendance);
        String day = batchNoToDay(dtAttendance.getBatchNo());
        synAttendanceAnalyzeService.synAttendanceAnalyze(day,day);
        return 1;
    }

    /**
     * 修改员工考勤记录
     *
     * @param dtAttendance 员工考勤记录
     * @return 结果
     */
    @Override
    public int updateDtAttendance(DtAttendance dtAttendance)
    {
        Long id = dtAttendance.getId();
        String place = dtAttendance.getPlace();
        Date nowDate = DateUtils.getNowDate();
        String batchNo = dtAttendance.getBatchNo();
        dtAttendance = new DtAttendance();
        dtAttendance.setId(id);
        dtAttendance.setPlace(place);
        dtAttendance.setUpdateTime(nowDate);
        dtAttendanceMapper.updateDtAttendance(dtAttendance);
        String day = batchNoToDay(batchNo);
        synAttendanceAnalyzeService.synAttendanceAnalyze(day,day);
        return 1;
    }

    /**
     * 批量删除员工考勤记录
     *
     * @param ids 需要删除的员工考勤记录主键
     * @return 结果
     */
    @Override
    public int deleteDtAttendanceByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0){
            throw new RuntimeException("主键不可为空!");
        }
        if (ids == null && ids.length >5){
            throw new RuntimeException("一次最多删除五条数据!");
        }
        AttendanceDao dao = new AttendanceDao();
        List<Long> longs = Arrays.asList(ids);
        dao.setIds(longs);
        List<DtAttendance> attendanceList = dtAttendanceMapper.selectDtAttendanceList(dao);
        if (CollectionUtils.isEmpty(attendanceList)){
            throw new RuntimeException("数据不存在!");
        }
        //batchNo集合,倒叙排列
        TreeSet<String> orderBatchNos = new TreeSet<>(Comparator.reverseOrder());
        for (DtAttendance dtAttendance : attendanceList) {
            String batchNo = dtAttendance.getBatchNo();
            if (StringUtils.isBlank(batchNo)){
                throw new RuntimeException("日期批次不可为空!");
            }
            orderBatchNos.add(batchNo);
        }
        Integer i = 0;
        for (String orderBatchNo : orderBatchNos) {
            String endDay = batchNoToDay(orderBatchNo);
            String beginDay = batchNoToDay(orderBatchNo);
            i = dtAttendanceMapper.deleteDtAttendanceByIds(ids);
            //同步数据
            synAttendanceAnalyzeService.synAttendanceAnalyze(beginDay,endDay);
        }
        return i;
    }

    private String batchNoToDay(String batchNo){
        if (StringUtils.isBlank(batchNo)){
            throw new RuntimeException("日期批次号不可为空!");
        }
        String y = batchNo.substring(0, 4);
        String m = batchNo.substring(4, 6);
        String d = batchNo.substring(6, 8);
        String endDay = y+"-"+m+"-"+d;
        return endDay;
    }
    /**
     * 删除员工考勤记录信息
     *
     * @param id 员工考勤记录主键
     * @return 结果
     */
    @Override
    public int deleteDtAttendanceById(Long id)
    {
        return dtAttendanceMapper.deleteDtAttendanceById(id);
    }

    @Override
    public void deleteByBatchNo(String batchNo) {
        dtAttendanceMapper.deleteByBatchNoRange("00000000",batchNo);
    }

    @Override
    public void batchInsertDtAttendance(List<DtAttendance> dtAttendances) {
        if(CollectionUtils.isNotEmpty(dtAttendances)){
            dtAttendanceMapper.batchInsertDtAttendance(dtAttendances);
        }
    }

    @Override
    public int batchDeleteByDataIds(List<String> list) {
        return dtAttendanceMapper.batchDeleteByDataIds(list);
    }
}
