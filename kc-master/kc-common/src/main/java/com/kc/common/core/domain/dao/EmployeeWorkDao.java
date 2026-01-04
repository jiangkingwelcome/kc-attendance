package com.kc.common.core.domain.dao;

import com.kc.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 查询钉钉考勤汇总查询条件类
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
@Data
public class EmployeeWorkDao extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 员工名称 */
    private String name;

    /** 工号 */
    private String jobNumber;

    /** 部门id */
    private String deptId;

}
