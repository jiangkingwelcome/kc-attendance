package com.kc.common.core.domain.dto;

import com.kc.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 员工页面分页
 *
 * @author zhaobo.yang
 * @date 2022-12-02
 */
@Data
public class EmployeePageReqeust extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 员工id */
    private String employeeId;

    /** 员工名称 */
    private String name;

    /** 工号 */
    private String jobNumber;
}
