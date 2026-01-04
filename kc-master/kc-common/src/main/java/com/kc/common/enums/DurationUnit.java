package com.kc.common.enums;

public enum DurationUnit {
    PERCENT_DAY_1("day", 1) ;

    private final String code;
    private final int value;

    DurationUnit(String code, int value)
    {
        this.code = code;
        this.value = value;
    }

    public String getCode()
    {
        return code;
    }

    public int getValue()
    {
        return value;
    }

    public static DurationUnit findByCode(String code) {
        for (DurationUnit type : DurationUnit.values()) {
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
