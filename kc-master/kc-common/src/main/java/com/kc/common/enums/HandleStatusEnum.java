package com.kc.common.enums;

/**
 * 用户状态
 *
 * @author kc
 */
public enum HandleStatusEnum {


    未处理_0(0, "未处理"),
    已处理_1(1, "已处理"),
    处理失败_2(2, "处理失败")
    ;

    private final int code;
    private final String info;

    HandleStatusEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
