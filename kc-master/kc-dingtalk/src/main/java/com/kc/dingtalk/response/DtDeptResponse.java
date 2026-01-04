package com.kc.dingtalk.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.kc.common.core.domain.BaseEntity;
import lombok.Data;


/**
 * 钉钉部门对象 dt_dept
 *
 * @author kc
 * @date 2022-11-03
 */
@Data
public class DtDeptResponse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 部门id */
    @JSONField(name = "dept_id")
    private Long deptId;

    /** 父部门id */
    @JSONField(name = "parent_id")
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 排序 */
    @JSONField(name = "company_type")
    private Integer companyType;

    /** 部门名称 */
    @JSONField(name = "name")
    private String deptName;

    /** 排序 */
    @JSONField(name = "order")
    private Integer orderNum;
}
