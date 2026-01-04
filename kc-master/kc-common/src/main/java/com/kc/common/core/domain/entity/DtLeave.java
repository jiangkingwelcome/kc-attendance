package com.kc.common.core.domain.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 员工请假审批单对象 dt_leave
 *
 * @author kc
 * @date 2023-10-08
 */
public class DtLeave extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 员工id
     */
    @Excel(name = "员工id")
    private String employeeId;

    /**
     * 员工姓名
     */
    @Excel(name = "员工姓名")
    private String name;

    /**
     * 日期批次
     */
    @Excel(name = "日期批次")
    private String batchNo;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /**
     * 开始时间（时间戳）
     */
    @Excel(name = "开始时间", readConverterExp = "时=间戳")
    private String startTimeStamp;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /**
     * 结束时间（时间戳）
     */
    @Excel(name = "结束时间", readConverterExp = "时=间戳")
    private String endTimeStamp;

    /**
     * 请假单位(0:小时,1:天)
     */
    @Excel(name = "请假单位(0:小时,1:天)")
    private String durationUnit;

    /**
     *
     */
    @Excel(name = "")
    private String leaveNo;

    /**
     * 删除标识
     */
    private String delFlag;

    /**
     * 唯一id(employee_id+startTimeStamp+endTimeStamp)
     */
    @Excel(name = "唯一id(employee_id+startTimeStamp+endTimeStamp)")
    private String dataId;

    private String batchNoStart;

    private String batchNoEnd;

    @Excel(name = "公司类型")
    private Integer companyType;

    /**
     * 假期时长
     */
    @Excel(name = "假期时长")
    private String duration;

    /**
     * 子类型名称
     */
    @Excel(name = "子类型名称")
    private String subType;

    /**
     * 审批单类型名称
     */
    @Excel(name = "审批单类型名称")
    private String tagName;

    /**
     * 审批单类型（1：加班，2：出差、外出 3：请假）
     */
    @Excel(name = "审批单类型", readConverterExp = "1=：加班，2：出差、外出,3=：请假")
    private Long bizType;

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBatchNoStart() {
        return batchNoStart;
    }

    public void setBatchNoStart(String batchNoStart) {
        this.batchNoStart = batchNoStart;
    }

    public String getBatchNoEnd() {
        return batchNoEnd;
    }

    public void setBatchNoEnd(String batchNoEnd) {
        this.batchNoEnd = batchNoEnd;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTimeStamp(String startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public String getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTimeStamp(String endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public String getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setLeaveNo(String leaveNo) {
        this.leaveNo = leaveNo;
    }

    public String getLeaveNo() {
        return leaveNo;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataId() {
        return dataId;
    }

    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setBizType(Long bizType) {
        this.bizType = bizType;
    }

    public Long getBizType() {
        return bizType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("employeeId", getEmployeeId())
                .append("name", getName())
                .append("batchNo", getBatchNo())
                .append("startTime", getStartTime())
                .append("startTimeStamp", getStartTimeStamp())
                .append("endTime", getEndTime())
                .append("endTimeStamp", getEndTimeStamp())
                .append("durationUnit", getDurationUnit())
                .append("leaveNo", getLeaveNo())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .append("dataId", getDataId())
                .append("companyType", getCompanyType())
                .append("duration", getDuration())
                .append("subType", getSubType())
                .append("tagName", getTagName())
                .append("bizType", getBizType())
                .toString();
    }
}

