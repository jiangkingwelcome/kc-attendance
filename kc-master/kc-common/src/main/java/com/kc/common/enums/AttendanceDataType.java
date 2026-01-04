package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum AttendanceDataType
{
    钉钉同步_0(0, "钉钉同步"),
    本地新增_1(1, "本地新增"),
    本地修改_1(2, "本地新增");
    private final int code;
    private final String info;

    AttendanceDataType(int code, String info)
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

    public static AttendanceDataType findByCode(int code) {
        for (AttendanceDataType type : AttendanceDataType.values()) {
            if (type.getCode() == code){
                return type;
            }
        }
        return null;
    }

}
