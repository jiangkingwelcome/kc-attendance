package com.kc.dingtalk.service.impl;

import com.kc.common.annotation.DataScope;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kc.dingtalk.mapper.DtEmployeeMapper;
import com.kc.dingtalk.service.IDtEmployeeService;
import java.util.ArrayList;
import java.util.List;


/**
 * 钉钉员工基础信息Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2022-11-26
 */
@Service
public class DtEmployeeServiceImpl implements IDtEmployeeService
{
    @Autowired
    private DtEmployeeMapper dtEmployeeMapper;

    /**
     * 查询钉钉员工基础信息
     *
     * @param id 钉钉员工基础信息主键
     * @return 钉钉员工基础信息
     */
    @Override
    public DtEmployee selectDtEmployeeById(Long id)
    {
        return dtEmployeeMapper.selectDtEmployeeById(id);
    }

    /**
     * 查询钉钉员工基础信息列表
     *
     * @param employeeQuery 钉钉员工基础信息
     * @return 钉钉员工基础信息
     */
    @Override
    @DataScope(userAlias = "ue",deptAlias = "d")
    public List<DtEmployee> selectDtEmployeeList(EmployeeDao employeeQuery)
    {
        return dtEmployeeMapper.selectDtEmployeeList(employeeQuery);
    }

    /**
     * 查询部门下的所有员工
     *
     * @param deptId 部门id
     * @return 钉钉员工基础信息
     */
    @Override
    public List<DtEmployee> findAllEmpByDeptId(Long deptId)
    {
       if (deptId == null){
           return new ArrayList<>();
       }
        return dtEmployeeMapper.findAllEmpByDeptId(deptId);
    }

    @Override
    public Boolean checkIdAndName(String employeeId, String employeeName) {
        if(StringUtils.isBlank(employeeId) ||StringUtils.isBlank(employeeName)){
            return false;
        }
        EmployeeDao employeeDao = new EmployeeDao();
        employeeDao.setName(employeeName);
        employeeDao.setEmployeeId(employeeId);
        List<DtEmployee> dtEmployees = dtEmployeeMapper.selectDtEmployeeList(employeeDao);
        return dtEmployees.size()>0;
    }

    /**
     * 新增钉钉员工基础信息
     *
     * @param dtEmployee 钉钉员工基础信息
     * @return 结果
     */
    @Override
    public int insertDtEmployee(DtEmployee dtEmployee)
    {
        dtEmployee.setCreateTime(DateUtils.getNowDate());
        return dtEmployeeMapper.insertDtEmployee(dtEmployee);
    }

    /**
     * 修改钉钉员工基础信息
     *
     * @param dtEmployee 钉钉员工基础信息
     * @return 结果
     */
    @Override
    public int updateDtEmployee(DtEmployee dtEmployee)
    {
        dtEmployee.setUpdateTime(DateUtils.getNowDate());
        return dtEmployeeMapper.updateDtEmployee(dtEmployee);
    }

    /**
     * 批量删除钉钉员工基础信息
     *
     * @param ids 需要删除的钉钉员工基础信息主键
     * @return 结果
     */
    @Override
    public int deleteDtEmployeeByIds(Long[] ids)
    {
        return dtEmployeeMapper.deleteDtEmployeeByIds(ids);
    }

    /**
     * 删除钉钉员工基础信息信息
     *
     * @param id 钉钉员工基础信息主键
     * @return 结果
     */
    @Override
    public int deleteDtEmployeeById(Long id)
    {
        return dtEmployeeMapper.deleteDtEmployeeById(id);
    }

    @Override
    public List<DtEmployee> selectDtEmployeeListNoAuth(EmployeeDao employeeQuery) {
        return dtEmployeeMapper.selectDtEmployeeList(employeeQuery);
    }

    @Override
    public int getThisMonthNewHireCount() {
        return dtEmployeeMapper.getThisMonthNewHireCount();
    }
}
