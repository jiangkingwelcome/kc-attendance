package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum CompanyType
{
    快仓自动化科技有限公司_0(0, "快仓自动化科技有限公司"),
    快仓智能科技有限公司_1(1, "快仓智能科技有限公司") ,
    潍坊众匠人信息技术有限公司_2(2, "潍坊众匠人信息技术有限公司"),

    宝仓智能科技苏州有限公司_3(3, "宝仓智能科技苏州有限公司");

    private final int code;
    private final String info;

    CompanyType(int code, String info)
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

    public static CompanyType findByCode(int code) {
        for (CompanyType type : CompanyType.values()) {
            if (type.getCode() == code){
                return type;
            }
        }
        return null;
    }
}
