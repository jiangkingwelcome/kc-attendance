package com.kc.common.core.domain.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 员工考勤记录对象 dt_attendance
 *
 * @author zhaobo.yang
 * @date 2022-12-02
 */
@Data
public class DtAttendance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 员工id */
    @Excel(name = "员工id")
    private String employeeId;

    /** 员工名称 */
    @Excel(name = "员工名称")
    private String name;

    /** 员工打卡时间（时间戳） */
    private String userCheckTimeStamp;

    /** 员工打卡时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "员工打卡时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date userCheckTime;

    /** 日期批次 */
    @Excel(name = "日期批次")
    private String batchNo;

    /** 类型(0:钉钉打卡,1:钉钉签到,2:补卡) */
    private Integer type;

    /** 公司类型(0:智能公司,1:自动化公司，2：潍坊,3:宝仓) */
    private Integer companyType;

    @Excel(name = "考勤地址")
    private String place;

    /** 数据类型(0:钉钉同步,1:新增) */
    private Integer dataType;

    private String dataId;

    /** 删除标志 */
    private String delFlag;
}
