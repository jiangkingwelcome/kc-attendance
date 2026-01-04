package com.kc.dingtalk.mapper;

import java.util.List;

import com.kc.common.core.domain.dao.EmployeeSaveDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.dao.EmployeeDao;

/**
 * 钉钉员工基础信息Mapper接口
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
public interface DtEmployeeMapper
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
     * @param query 查询钉钉员工基础信息
     * @return 钉钉员工基础信息集合
     */
     List<DtEmployee> selectDtEmployeeList(EmployeeDao query);

    /**
     * 新增钉钉员工基础信息
     *
     * @param dtEmployee 钉钉员工基础信息
     * @return 结果
     */
     int insertDtEmployee(DtEmployee dtEmployee);

    /**
     * 批量新增部门
     *
     * @param dtEmployeeList 钉钉员工列表
     * @return 结果
     */
    int batchInsertDtEmployee(List<EmployeeSaveDao> dtEmployeeList);

    /**
     * 修改钉钉员工基础信息
     *
     * @param dtEmployee 钉钉员工基础信息
     * @return 结果
     */
     int updateDtEmployee(DtEmployee dtEmployee);

    /**
     * 删除钉钉员工基础信息
     *
     * @param id 钉钉员工基础信息主键
     * @return 结果
     */
     int deleteDtEmployeeById(Long id);

    /**
     * 批量删除钉钉员工基础信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
     int deleteDtEmployeeByIds(Long[] ids);

    /**
     * 删除所有员工
     */
    int deleteByEmployeeIds(List<String> employeeIds);

    /**
     * @description:  获得未创建用户的钉钉人员
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/12/15 14:37
     */
    List<DtEmployee> getUnCreateAccountEmployees();

    /**
     * 获取部门下的所有人员
     *
     * @param deptId 部门id
     * @return 钉钉员工基础信息集合
     */
    List<DtEmployee> findAllEmpByDeptId(Long deptId);

    /**
     * 全部离职
     */
    void dimissionAll();

    void batchUpdate(List<DtEmployee> list);

}
