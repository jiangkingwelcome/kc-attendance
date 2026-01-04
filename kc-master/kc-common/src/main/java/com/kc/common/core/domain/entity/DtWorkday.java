package com.kc.common.core.domain.entity;

import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 员工考勤日期对象 dt_workday
 *
 * @author zhaobo.yang
 * @date 2023-10-18
 */
public class DtWorkday extends BaseEntity
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

    /** 日期批次 */
    @Excel(name = "日期批次")
    private String batchNo;

    /** 唯一标识id(user_id+batch_no) */
    @Excel(name = "唯一标识id(user_id+batch_no)")
    private String dataId;

    /** 删除标识 */
    private String delFlag;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String date;

    /** 工号 */
    @Excel(name = "工号")
    private String jobNumber;

    /** 开始日期批次 */
    private String beginBatchNo;

    /** 结束日期批次 */
    private String endBatchNo;

    public String getBeginBatchNo() {
        return beginBatchNo;
    }

    public void setBeginBatchNo(String beginBatchNo) {
        this.beginBatchNo = beginBatchNo;
    }

    public String getEndBatchNo() {
        return endBatchNo;
    }

    public void setEndBatchNo(String endBatchNo) {
        this.endBatchNo = endBatchNo;
    }

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
    public void setDataId(String dataId)
    {
        this.dataId = dataId;
    }

    public String getDataId()
    {
        return dataId;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }
    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDate()
    {
        return date;
    }
    public void setJobNumber(String jobNumber)
    {
        this.jobNumber = jobNumber;
    }

    public String getJobNumber()
    {
        return jobNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("employeeId", getEmployeeId())
                .append("name", getName())
                .append("batchNo", getBatchNo())
                .append("dataId", getDataId())
                .append("delFlag", getDelFlag())
                .append("date", getDate())
                .append("jobNumber", getJobNumber())
                .toString();
    }
}
