package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum WorkOrderStatusEnum
{
    NEW("NEW", "新创建"),
    RUNNING("RUNNING", "审批中"),
    TERMINATED("TERMINATED", "被终止"),
    COMPLETED("COMPLETED", "完成"),
    CANCELED("CANCELED", "取消");

    private final String code;
    private final String info;

    WorkOrderStatusEnum(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
