package com.kc.common.core.domain.dao;

import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 钉钉补卡工单对象 dt_mendcard_order
 *
 * @author zhaobo.yang
 * @date 2023-07-04
 */
public class DtMendcardOrder extends BaseEntity
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

    /** 工单实例id */
    @Excel(name = "工单实例id")
    private String instanceId;

    /** NEW：新创建,RUNNING：审批中,TERMINATED：被终止,COMPLETED：完成,CANCELED：取消 */
    @Excel(name = "NEW：新创建,RUNNING：审批中,TERMINATED：被终止,COMPLETED：完成,CANCELED：取消")
    private String status;

    /** agree：同意,refuse：拒绝 */
    @Excel(name = "agree：同意,refuse：拒绝")
    private String result;

    /** 0:未处理,1:已处理,2:处理失败 */
    @Excel(name = "0:未处理,1:已处理,2:处理失败")
    private Integer handleStatus;

    /** 员工补卡时间（时间戳） */
    @Excel(name = "员工补卡时间", readConverterExp = "时间戳")
    private String userCheckTime;

    /** 工单实例ids */
    private List<String> instanceIds;

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

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
    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getInstanceId()
    {
        return instanceId;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setResult(String result)
    {
        this.result = result;
    }

    public String getResult()
    {
        return result;
    }
    public void setHandleStatus(Integer handleStatus)
    {
        this.handleStatus = handleStatus;
    }

    public Integer getHandleStatus()
    {
        return handleStatus;
    }
    public void setUserCheckTime(String userCheckTime)
    {
        this.userCheckTime = userCheckTime;
    }

    public String getUserCheckTime()
    {
        return userCheckTime;
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("employeeId", getEmployeeId())
            .append("name", getName())
            .append("instanceId", getInstanceId())
            .append("status", getStatus())
            .append("result", getResult())
            .append("handleStatus", getHandleStatus())
            .append("userCheckTime", getUserCheckTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
