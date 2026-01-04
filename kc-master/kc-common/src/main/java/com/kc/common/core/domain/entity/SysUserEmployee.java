package com.kc.common.core.domain.entity;

import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户和员工对象 sys_user_employee
 *
 * @author kc
 * @date 2022-12-15
 */
public class SysUserEmployee extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 员工ID */
    private String employeeId;

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setEmployeeId(String employeeId)
    {
        this.employeeId = employeeId;
    }

    public String getEmployeeId()
    {
        return employeeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("employeeId", getEmployeeId())
            .toString();
    }
}
