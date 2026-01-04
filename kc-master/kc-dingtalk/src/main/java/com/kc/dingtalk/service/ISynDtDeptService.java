package com.kc.dingtalk.service;


import com.kc.dingtalk.response.DtDeptResponse;
import java.util.List;

/**
 * 同步钉钉部门信息接口
 *
 * @author zhaobo
 * @date 2022-11-03
 */
public interface ISynDtDeptService
{
    /**
     * @description:  同步部门
     * @param
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/27 2:48
     */
    void synDtDept();


    /**
     * @description:
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/27 2:48
     */
    List<DtDeptResponse> getDeptByParentId(Long parentId, String ancestors, int companyType);
}
