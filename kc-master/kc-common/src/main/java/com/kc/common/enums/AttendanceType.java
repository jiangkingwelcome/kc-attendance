package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum AttendanceType
{
    打卡记录_0(0, "钉钉打卡"), 签到记录_1(1, "签到记录"),补卡记录_2(2, "补卡记录"),请假记录_3(3, "请假记录");

    private final int code;
    private final String info;

    AttendanceType(int code, String info)
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
