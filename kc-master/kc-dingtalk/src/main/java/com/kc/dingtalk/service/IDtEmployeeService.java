package com.kc.dingtalk.service;


import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtEmployee;
import java.util.List;

/**
 * 钉钉员工基础信息Service接口
 *
 * @author zhaobo.yang
 * @date 2022-11-26
 */
public interface IDtEmployeeService
{
    /**
     * 查询钉钉员工基础信息
     *
     * @param id 钉钉员工基础信息主键
     * @return 钉钉员工基础信息
     */
    DtEmployee selectDtEmployeeById(Long id);

    /**
     * 查询钉钉员工基础信息列表
     *
     * @param query 钉钉员工基础信息
     * @return 钉钉员工基础信息集合
     */
     List<DtEmployee> selectDtEmployeeList(EmployeeDao query);

    /**
     * 校验员工id和姓名是否匹配
     */
    Boolean checkIdAndName(String employeeId,String employeeName);


    /**
     * 新增钉钉员工基础信息
     *
     * @param dtEmployee 钉钉员工基础信息
     * @return 结果
     */
    public int insertDtEmployee(DtEmployee dtEmployee);

    /**
     * 修改钉钉员工基础信息
     *
     * @param dtEmployee 钉钉员工基础信息
     * @return 结果
     */
    public int updateDtEmployee(DtEmployee dtEmployee);

    /**
     * 批量删除钉钉员工基础信息
     *
     * @param ids 需要删除的钉钉员工基础信息主键集合
     * @return 结果
     */
    public int deleteDtEmployeeByIds(Long[] ids);

    /**
     * 删除钉钉员工基础信息信息
     *
     * @param id 钉钉员工基础信息主键
     * @return 结果
     */
    public int deleteDtEmployeeById(Long id);

    /**
     * @description:  获取部门下的所有员工
     * @param deptId 部门id
     * @author: zhaobo.yang
     * @date: 2023/7/10 18:10
     */
    List<DtEmployee> findAllEmpByDeptId(Long deptId);

    /**
     * 查询钉钉员工基础信息列表
     *
     * @param employeeQuery 钉钉员工基础信息
     * @return 钉钉员工基础信息
     */
    List<DtEmployee> selectDtEmployeeListNoAuth(EmployeeDao employeeQuery);

    /**
     * 查询本月新入职人数
     *
     * @return 本月新入职人数
     */
    int getThisMonthNewHireCount();

}
