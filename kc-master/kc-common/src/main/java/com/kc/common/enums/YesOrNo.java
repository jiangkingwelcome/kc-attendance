package com.kc.common.enums;

/**
 * 是否
 *
 * @author kc
 */
public enum YesOrNo
{
    是_1("1", "是") ,
    否_0("0", "否") ;

    private final String code;
    private final String info;

    YesOrNo(String code, String info)
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
