package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum AgentIdEnum
{
    快仓智能科技有限公司_1(1, 1401240858L) ,
    //快仓智能科技有限公司_1(1, 1652260283L) ,

    宝仓智能科技苏州有限公司_3(3, 1818302883L);

    private final int code;
    private final Long info;

    AgentIdEnum(int code, Long info)
    {
        this.code = code;
        this.info = info;
    }

    public int getCode()
    {
        return code;
    }

    public Long getInfo()
    {
        return info;
    }

    public static AgentIdEnum findByCode(int code) {
        for (AgentIdEnum type : AgentIdEnum.values()) {
            if (type.getCode() == code){
                return type;
            }
        }
        return null;
    }
}
