package com.kc.common.core.domain.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 查询钉钉员工信息
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
@Data
public class EmployeeDao extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 员工id */
    private String employeeId;

    /** 员工名称 */
    private String name;

    /** 部门id */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 人员ids */
    private List<String> employeeIds;

    /** 部门ids */
    private List<Long> deptIds;

    private String departments;

    /** 员工工号 */
    private String jobNumber;

    /** 主键id */
    private Long id;

    /** 企业账号内的唯一编码 */
    private String unionId;

    /** 手机号码 */
    private String mobile;

    /** 分机号 */
    private String tel;

    /** 办公地点 */
    private String workPlace;

    /** 是否是管理员 */
    private String canAdmin;

    /** 是否为企业老板 */
    private String canBoss;

    /** 是否号码隐藏 */
    private String canHide;

    /** 职位信息 */
    private String position;

    /** 员工邮箱 */
    private String email;

    /** 员工企业邮箱 */
    private String orgEmail;

    /** 头像 */
    private String avatar;

    /** 入职时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date hiredDate;

    /** 拓展属性 */
    private String extattr;

    /** 离职标志 */
    private String canDimission;

    /** 修改标识 */
    private String updateFlag;

    /** 删除标识 */
    private String delFlag;

    /** 唯一id */
    private  String dataId;

    /** 公司类型(0:自动化,1:智能,2:潍坊，3:宝仓) */
    private Integer companyType;

    /** 主部门 */
    private Long mainDept;

    /** 身份证号 */
    private String cardNum;

    /** 员工类型 */
    private Integer empType;

    /** 离职日期 */
    private Date lastWorkDay;


}
