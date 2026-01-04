package com.kc.common.core.domain.dao;

import com.kc.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 员工考勤汇总分析对象 dt_attendance_analyze
 *
 * @author zhaobo.yang
 * @date 2022-11-08
 */
@Data
public class AttendanceAnalyzeDao extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */

    private Long id;

    /** 员工id */
    private String employeeId;

    /** 员工名称 */
    private String name;

    /** 工号 */
    private String jobNumber;

    /** 上班时间 */
    private Date workTime;

    /** 下班时间 */
    private Date offWorkTime;

    /** 日期批次 */
    private String batchNo;

    /** 日期批次 */
    private String duration;

    /** 用户ids */
    private List<String> employeeIds;

    /** 状态(0:正常,1异常,2周六缺卡,3周天缺卡) */
    private Integer status;

    /** 日期批次起始时间 */
    private String batchNoStart;

    /** 日期批次结束时间 */
    private String batchNoEnd;

    /** 部门id */
    private Long deptId;

    /** 考勤地址 */
    private String place;

    /** 删除标志 */
    private String delFlag;

    /** 请假记录 */
    private String leaveRecord;

    /** 请假时长 */
    private String leaveDuration;

    /** 请假次数 */
    private String leaveCount;

    /** 请假记录 */
    private String travelRecord;

    /** 请假时长 */
    private String travelDuration;

    /** 请假次数 */
    private String travelCount;

    /** 加班类型 */
    private Integer overtimeType;

    /** 加班时长 */
    private String overtimeDuration;

    /** 用户名称 */
    private List<String> employeeNames;

    /** 人员工号集合 */
    private List<String> jobNumbers;

    private  Integer empType;


}
