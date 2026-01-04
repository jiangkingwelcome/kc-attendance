package com.kc.dingtalk.service;


import java.util.Date;

/**
 * 同步钉钉用户信息接口
 *
 * @author zhaobo
 * @date 2022-11-03
 */
public interface ISynDtEmployeeService
{
    void synDtEmployee();

    /**
     * @description:  根据钉钉人员批量开通用户账号
     * @param
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/12/15 14:22
     */
    void batchCreateUser();

    /**
     * @description:  同步员工身份证和工号和部门信息
     * @param companyType
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/12/27 14:22
     */

    void synIdCardAndJobNumAndMainDept(Integer companyType);


    /**
     * @description:  同步员工离职时间
     * @author: zhaobo.yang
     * @date: 2022/12/27 14:22
     */
    void synEmployeeDismissionInfo(String startDateStr, String endDateStr, Integer companyType);

    /**
     * @description:  设置员工人员类型
     * @author: zhaobo.yang
     * @date: 2022/12/27 14:22
     */
    void handleEmpType();
}
