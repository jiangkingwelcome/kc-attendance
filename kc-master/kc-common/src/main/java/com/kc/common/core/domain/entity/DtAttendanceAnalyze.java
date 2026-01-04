package com.kc.common.core.domain.entity;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 员工考勤汇总分析对象 dt_attendance_analyze
 *
 * @author zhaobo.yang
 * @date 2022-11-08
 */
@Data
public class DtAttendanceAnalyze extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 员工id */
    private String employeeId;

    /** 员工名称 */
    @Excel(name = "员工名称")
    private String name;

    /** 工号 */
    @Excel(name = "工号")
    private String jobNumber;

    /** 员工类型 */
    @Excel(name = "员工类型",dictType = "emp_type")
    private Integer empType;

    /** 上班时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上班时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date workTime;

    /** 下班时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下班时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date offWorkTime;

    /** 日期批次 */
    @Excel(name = "日期批次")
    private String batchNo;

    /** 日期批次 */
    @Excel(name = "工时")
    private String duration;

    /** 员工ids */
    private List<String> employeeIds;

    /** 状态(0:正常,1异常,2周六缺卡,3周天缺卡) */
    @Excel(name = "状态",dictType="dt_attendance_analyze_status")
    private Integer status;

    /** 考勤地址 */
    @Excel(name = "上班地点|下班地点")
    private String place;

    /** 请假记录 */
    @Excel(name = "请假记录")
    private String leaveRecord;

    /** 请假时长(天) */
    @Excel(name = "请假时长(天)")
    private String leaveDuration;

    /** 请假次数 */
    @Excel(name = "请假次数")
    private String leaveCount;

    /** 出差记录 */
    @Excel(name = "出差记录")
    private String travelRecord;

    /** 出差时长(小时) */
    @Excel(name = "出差时长(小时)")
    private String travelDuration;

    /** 出差次数 */
    @Excel(name = "出差次数")
    private String travelCount;

    /** 加班时长(天) */
    @Excel(name = "加班类型",dictType = "overtime_type")
    private Integer overtimeType;

    /** 出差时长(小时) */
    @Excel(name = "加班时长(天)")
    private String overtimeDurationDay;

    /** 出差时长(小时) */
    @Excel(name = "加班时长(小时)")
    private String overtimeDurationHour;

    /** 删除标志 */
    private String delFlag;

    //入职日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date hiredDate;

    //离职日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "离职日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastWorkDay;

}
