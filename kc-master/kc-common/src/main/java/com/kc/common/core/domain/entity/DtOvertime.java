package com.kc.common.core.domain.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 员工加班记录对象 dt_overtime
 *
 * @author zhaobo.yang
 * @date 2023-10-19
 */
public class DtOvertime extends BaseEntity {
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
     * 员工名称
     */
    @Excel(name = "员工名称")
    private String name;

    /**
     * 日期批次
     */
    @Excel(name = "日期批次")
    private String batchNo;

    /**
     * 类型(0:上午,1:下午,2:全天)
     */
    @Excel(name = "类型(0:上午,1:下午,2:全天)")
    private Integer type;

    /**
     * 时长
     */
    @Excel(name = "时长")
    private String duration;

    /**
     * 唯一标识id(user_id+batch_no+type)
     */
    @Excel(name = "唯一标识id(user_id+batch_no+type)")
    private String dataId;

    /**
     * 删除标识
     */
    private String delFlag;

    /**
     * 公司类型
     */
    private Integer companyType;

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

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("employeeId", getEmployeeId())
                .append("name", getName())
                .append("batchNo", getBatchNo())
                .append("type", getType())
                .append("duration", getDuration())
                .append("companyType", getCompanyType())
                .append("dataId", getDataId())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
