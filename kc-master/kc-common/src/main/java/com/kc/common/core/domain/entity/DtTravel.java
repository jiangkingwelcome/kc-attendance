package com.kc.common.core.domain.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 外出记录对象 dt_travel
 *
 * @author kc
 * @date 2024-05-06
 */
/**
 * 外出记录对象 dt_travel
 *
 * @author kc
 * @date 2024-05-07
 */
public class DtTravel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 员工id */
    @Excel(name = "员工id")
    private String employeeId;

    /** 员工姓名 */
    @Excel(name = "员工姓名")
    private String name;

    /** 日期批次 */
    @Excel(name = "日期批次")
    private String batchNo;

    /** 单据号 */
    @Excel(name = "单据号")
    private String businessCode;

    /** 版本 */
    @Excel(name = "版本")
    private String version;

    /** 状态1001-初始,1002-提交审批,1003-审批通过,1012-已报废 */
    @Excel(name = "状态1001-初始,1002-提交审批,1003-审批通过,1012-已报废")
    private String orderStatus;

    /** 拒绝类型:1000-正常, 1001-撤回, 1002-审批驳回 1003-财务驳回 1004-开票驳回 */
    @Excel(name = "拒绝类型:1000-正常, 1001-撤回, 1002-审批驳回 1003-财务驳回 1004-开票驳回")
    private String rejectType;

    /** 出差名称 */
    @Excel(name = "出差名称")
    private String formName;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 单据开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "单据开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderStartTime;

    /** 单据结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "单据结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderEndTime;

    /** 出差时长(小时) */
    @Excel(name = "出差时长(小时)")
    private String duration;

    /** 删除标识 */
    private String delFlag;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setEmployeeId(String employeeId)
    {
        this.employeeId = employeeId;
    }

    public String getEmployeeId()
    {
        return employeeId;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo()
    {
        return batchNo;
    }
    public void setBusinessCode(String businessCode)
    {
        this.businessCode = businessCode;
    }

    public String getBusinessCode()
    {
        return businessCode;
    }
    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }
    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }
    public void setRejectType(String rejectType)
    {
        this.rejectType = rejectType;
    }

    public String getRejectType()
    {
        return rejectType;
    }
    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    public String getFormName()
    {
        return formName;
    }
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
    public void setOrderStartTime(Date orderStartTime)
    {
        this.orderStartTime = orderStartTime;
    }

    public Date getOrderStartTime()
    {
        return orderStartTime;
    }
    public void setOrderEndTime(Date orderEndTime)
    {
        this.orderEndTime = orderEndTime;
    }

    public Date getOrderEndTime()
    {
        return orderEndTime;
    }
    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public String getDuration()
    {
        return duration;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("employeeId", getEmployeeId())
                .append("name", getName())
                .append("batchNo", getBatchNo())
                .append("businessCode", getBusinessCode())
                .append("version", getVersion())
                .append("orderStatus", getOrderStatus())
                .append("rejectType", getRejectType())
                .append("formName", getFormName())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("orderStartTime", getOrderStartTime())
                .append("orderEndTime", getOrderEndTime())
                .append("duration", getDuration())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
