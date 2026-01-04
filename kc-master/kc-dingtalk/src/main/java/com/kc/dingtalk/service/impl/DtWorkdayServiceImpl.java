package com.kc.dingtalk.service.impl;

import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.DtWorkday;
import com.kc.common.enums.YesOrNo;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.service.IDtEmployeeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kc.dingtalk.mapper.DtWorkdayMapper;
import com.kc.dingtalk.service.IDtWorkdayService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 员工考勤日期Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2023-10-18
 */
@Service
public class DtWorkdayServiceImpl implements IDtWorkdayService
{
    @Autowired
    private DtWorkdayMapper dtWorkdayMapper;
    @Autowired
    private IDtEmployeeService employeeService;

    /**
     * 查询员工考勤日期
     *
     * @param id 员工考勤日期主键
     * @return 员工考勤日期
     */
    @Override
    public DtWorkday selectDtWorkdayById(Long id)
    {
        return dtWorkdayMapper.selectDtWorkdayById(id);
    }

    /**
     * 查询员工考勤日期列表
     *
     * @param dtWorkday 员工考勤日期
     * @return 员工考勤日期
     */
    @Override
    public List<DtWorkday> selectDtWorkdayList(DtWorkday dtWorkday)
    {
        return dtWorkdayMapper.selectDtWorkdayList(dtWorkday);
    }

    /**
     * 新增员工考勤日期
     *
     * @param dtWorkday 员工考勤日期
     * @return 结果
     */
    @Override
    public int insertDtWorkday(DtWorkday dtWorkday)
    {
        return dtWorkdayMapper.insertDtWorkday(dtWorkday);
    }

    /**
     * 修改员工考勤日期
     *
     * @param dtWorkday 员工考勤日期
     * @return 结果
     */
    @Override
    public int updateDtWorkday(DtWorkday dtWorkday)
    {
        return dtWorkdayMapper.updateDtWorkday(dtWorkday);
    }

    /**
     * 批量删除员工考勤日期
     *
     * @param ids 需要删除的员工考勤日期主键
     * @return 结果
     */
    @Override
    public int deleteDtWorkdayByIds(Long[] ids)
    {
        return dtWorkdayMapper.deleteDtWorkdayByIds(ids);
    }

    /**
     * 删除员工考勤日期信息
     *
     * @param id 员工考勤日期主键
     * @return 结果
     */
    @Override
    public int deleteDtWorkdayById(Long id)
    {
        return dtWorkdayMapper.deleteDtWorkdayById(id);
    }

    @Override
    public int batchInsert(List<DtWorkday> list) {
        return dtWorkdayMapper.batchInsert(list);
    }

    @Override
    public int insertDateBy(Date startDate,Date endDate) {
        //获取所有人员
        EmployeeDao queryDao = new EmployeeDao();
        queryDao.setCanDimission(YesOrNo.否_0.getCode());
        List<DtEmployee> employees = employeeService.selectDtEmployeeListNoAuth(queryDao);
        if (CollectionUtils.isEmpty(employees)){
            return 0;
        }
        List<DtWorkday> workdays = new ArrayList<>();
        while (!startDate.after(endDate)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            Date time = calendar.getTime();
            String batchNo = DateUtils.parseDateToStr("yyyyMMdd", time);
            String date = DateUtils.parseDateToStr("yyyy-MM-dd", time);
            for (DtEmployee employee : employees) {
                String employeeId = employee.getEmployeeId();
                String name = employee.getName();
                String jobNumber = employee.getJobNumber();
                if (StringUtils.isBlank(jobNumber)){
                    continue;
                }
                String dataId = employeeId+batchNo;
                DtWorkday dtWorkday = new DtWorkday();
                dtWorkday.setEmployeeId(employeeId);
                dtWorkday.setJobNumber(jobNumber);
                dtWorkday.setName(name);
                dtWorkday.setBatchNo(batchNo);
                dtWorkday.setDataId(dataId);
                dtWorkday.setDelFlag("0");
                dtWorkday.setDate(date);
                workdays.add(dtWorkday);
            }
            calendar.add(Calendar.DAY_OF_MONTH,1);
            startDate =calendar.getTime();
        }
        int result = 0;
        if (CollectionUtils.isNotEmpty(workdays)){
            result =  batchInsert(workdays);
        }
        return result;
    }
}
