package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum OverTimeType
{
    半天_0(0, "半天"),全天_1(1, "全天"),小时_2(2, "全天");

    private final int code;
    private final String info;

    OverTimeType(int code, String info)
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

    public static OverTimeType findByInfo(String info) {
        for (OverTimeType type : OverTimeType.values()) {
            if (type.getInfo().equals(info)){
                return type;
            }
        }
        return null;
    }
}
