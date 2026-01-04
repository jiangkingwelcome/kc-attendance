package com.kc.dingtalk.mapper;

import com.kc.common.core.domain.dao.EmployeeWorkDao;
import com.kc.common.core.domain.entity.DtEmployeeWork;

import java.util.List;

/**
 * 员工工作统计Mapper接口
 *
 * @author kc
 * @date 2025-08-03
 */
public interface DtEmployeeWorkMapper
{
    /**
     * 查询员工工作统计
     *
     * @return 员工工作统计
     */
    public List<DtEmployeeWork> selectEmployeeWorkStat(EmployeeWorkDao employeeWorkDao);


}
