package com.kc.system.mapper;

import com.kc.common.core.domain.entity.SysUserEmployee;

import java.util.List;

/**
 * 用户和员工Mapper接口
 *
 * @author kc
 * @date 2022-12-15
 */
public interface SysUserEmployeeMapper
{
    /**
     * 查询用户和员工
     *
     * @param userId 用户和员工主键
     * @return 用户和员工
     */
    public SysUserEmployee selectSysUserEmployeeByUserId(Long userId);

    /**
     * 查询用户和员工列表
     *
     * @param sysUserEmployee 用户和员工
     * @return 用户和员工集合
     */
    public List<SysUserEmployee> selectSysUserEmployeeList(SysUserEmployee sysUserEmployee);

    /**
     * 新增用户和员工
     *
     * @param sysUserEmployee 用户和员工
     * @return 结果
     */
    public int insertSysUserEmployee(SysUserEmployee sysUserEmployee);

    /**
     * 修改用户和员工
     *
     * @param sysUserEmployee 用户和员工
     * @return 结果
     */
    public int updateSysUserEmployee(SysUserEmployee sysUserEmployee);

    /**
     * 删除用户和员工
     *
     * @param userId 用户和员工主键
     * @return 结果
     */
    public int deleteSysUserEmployeeByUserId(Long userId);

    /**
     * 批量删除用户和员工
     *
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysUserEmployeeByUserIds(Long[] userIds);
}
