package com.kc.system.service.impl;

import java.util.List;

import com.kc.common.core.domain.entity.SysUserEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kc.system.mapper.SysUserEmployeeMapper;
import com.kc.system.service.ISysUserEmployeeService;

/**
 * 用户和员工Service业务层处理
 *
 * @author kc
 * @date 2022-12-15
 */
@Service
public class SysUserEmployeeServiceImpl implements ISysUserEmployeeService
{
    @Autowired
    private SysUserEmployeeMapper sysUserEmployeeMapper;

    /**
     * 查询用户和员工
     *
     * @param userId 用户和员工主键
     * @return 用户和员工
     */
    @Override
    public SysUserEmployee selectSysUserEmployeeByUserId(Long userId)
    {
        return sysUserEmployeeMapper.selectSysUserEmployeeByUserId(userId);
    }

    /**
     * 查询用户和员工列表
     *
     * @param sysUserEmployee 用户和员工
     * @return 用户和员工
     */
    @Override
    public List<SysUserEmployee> selectSysUserEmployeeList(SysUserEmployee sysUserEmployee)
    {
        return sysUserEmployeeMapper.selectSysUserEmployeeList(sysUserEmployee);
    }

    /**
     * 新增用户和员工
     *
     * @param sysUserEmployee 用户和员工
     * @return 结果
     */
    @Override
    public int insertSysUserEmployee(SysUserEmployee sysUserEmployee)
    {
        return sysUserEmployeeMapper.insertSysUserEmployee(sysUserEmployee);
    }

    /**
     * 修改用户和员工
     *
     * @param sysUserEmployee 用户和员工
     * @return 结果
     */
    @Override
    public int updateSysUserEmployee(SysUserEmployee sysUserEmployee)
    {
        return sysUserEmployeeMapper.updateSysUserEmployee(sysUserEmployee);
    }

    /**
     * 批量删除用户和员工
     *
     * @param userIds 需要删除的用户和员工主键
     * @return 结果
     */
    @Override
    public int deleteSysUserEmployeeByUserIds(Long[] userIds)
    {
        return sysUserEmployeeMapper.deleteSysUserEmployeeByUserIds(userIds);
    }

    /**
     * 删除用户和员工信息
     *
     * @param userId 用户和员工主键
     * @return 结果
     */
    @Override
    public int deleteSysUserEmployeeByUserId(Long userId)
    {
        return sysUserEmployeeMapper.deleteSysUserEmployeeByUserId(userId);
    }
}
