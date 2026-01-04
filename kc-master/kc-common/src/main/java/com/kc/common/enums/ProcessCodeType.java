package com.kc.common.enums;

public enum ProcessCodeType {

    补卡("1", "PROC-FF6YHERSO2-0Z2FS0PTM7OC500C8W0Z1-4L5MCLUI-Z1") ,
    请假("0", "PROC-EF6Y0XWVO2-SHF7BVVDVTDSO0NCBHLI3-SM4PNTJI-O3") ;

    private final String code;
    private final String info;

    ProcessCodeType(String code, String info)
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
