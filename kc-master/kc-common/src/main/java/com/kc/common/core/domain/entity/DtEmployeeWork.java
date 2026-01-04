package com.kc.common.core.domain.entity;

import java.math.BigDecimal;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 员工工作统计对象 dt_employee_work
 *
 * @author kc
 * @date 2025-08-03
 */
@Data
public class DtEmployeeWork extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 员工姓名 */
    @Excel(name = "员工姓名")
    private String name;

    @Excel(name = "工号")
    /** 工号（唯一标识） */
    private String jobNumber;

    @Excel(name = "人员类型",dictType = "emp_type")
    private Integer empType;

    @Excel(name = "部门")
    /** 部门 */
    private String deptNameChain;

    /** 总时长 */
    @Excel(name = "考勤总工时(不含加班、请假)")
    private String durationtotal;

    /** 总天数 */
    @Excel(name = "考勤天数(不含加班、请假)")
    private String durationdays;

    /** 平均时长 */
    @Excel(name = "考勤平均工时（小时）")
    private String avgduration;

    /** 计数 */
    @Excel(name = "应出勤天数(天)")
    private String ycounts;

    /** 计数 */
    @Excel(name = "应出未出天数(天)")
    private String ywcounts;

    /** 天数 */
    @Excel(name = "应出未出日期")
    private String ywdays;

    /** 全部时长  */
    @Excel(name = "总工时(含加班、请假半天)")
    private String allduration;

    /** 计数 */
    @Excel(name = "实际出勤天数(天)")
    private String scount;

    /** 平均时长 */
    @Excel(name = "平均工时（小时）")
    private String savgduration;

    /** 请假次数 */
    @Excel(name = "请假次数")
    private String leavecount;

    /** 请假时长 */
    @Excel(name = "请假时长(天)")
    private String leaveduration;

    /** 半天请假时长 */
    @Excel(name = "半天假考勤时长（小时）")
    private String leavehalfdayduration;

    /** 加班次数 */
    @Excel(name = "加班次数")
    private String overtimecount;

    /** 申请加班天数 */
    @Excel(name = "加班申请时长（天）")
    private String overtimeapplydurationDay;

    /** 申请加班小时数 */
    @Excel(name = "加班申请时长（小时）")
    private String overtimeapplydurationHour;

    /** 实际加班时长 */
    @Excel(name = "加班打卡时长(小时)")
    private String overtimeduration;

    /** 出差次数 */
    @Excel(name = "出差次数")
    private String travelcount;

    /** 出差时长 */
    @Excel(name = "出差时长(小时)")
    private String travelduration;

    /** 出差天数 */
    @Excel(name = "出差时长（天）")
    private String traveldays;

    /** 迟到&工时不足(当天请假,加班,出差不计入) */
    @Excel(name = "迟到&工时不足(天数)")
    private String lateworkUndertime;

    /** 迟到&工时满足(当天请假,加班,出差不计入) */
    @Excel(name = "迟到&工时满足(天数)")
    private String lateworkAbovetime;

    /** 准时&工时不足(当天请假,加班,出差不计入) */
    @Excel(name = "准时&工时不足(天数)")
    private String earlyworkUndertime;

    //入职时间

    @Excel(name = "入职日期", dateFormat = "yyyy-MM-dd")
    private String hiredDate;

    //离职时间
    @Excel(name = "离职日期", dateFormat = "yyyy-MM-dd")
    private String lastWorkDay;


    private String employeeId;
}
