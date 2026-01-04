package com.kc.common.core.domain.dto;

import com.kc.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.List;

/**
 * 查询钉钉考勤汇总查询条件类
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
@Data
public class AttendanceAnalyzePageRequest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 员工id */
    private String employeeId;

    /** 员工名称 */
    private String name;

    /** 状态(0:正常,1异常,2周六缺卡,3周天缺卡) */
    private Integer status;

    /** 工号 */
    private String jobNumber;

    /** 用户ids */
    private List<String> employeeIds;

    /** 用户名称 */
    private List<String> employeeNames;
}
