package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum AttendanceAnalyzeStatus
{
    正常_0(0, "正常"),
    异常_1(1, "异常"),
    周六仅打一次卡_2(2, "周六仅打一次卡"),
    周日仅打一次卡_3(3, "周日仅打一次卡");

    private final int code;
    private final String info;

    AttendanceAnalyzeStatus(int code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public int getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
