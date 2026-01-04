package com.kc.common.core.domain.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 钉钉员工基础信息对象 dt_employee
 *
 * @author zhaobo.yang
 * @date 2022-12-08
 */
@Data
public class DtEmployee extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 员工id */
    @Excel(name = "员工id")
    private String employeeId;

    /** 员工名称 */
    @Excel(name = "员工名称")
    private String name;

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

    /** 所属部门 */
    private String departments;

    /** 职位信息 */
    private String position;

    /** 员工邮箱 */
    private String email;

    /** 员工企业邮箱 */
    private String orgEmail;

    /** 头像 */
    private String avatar;

    /** 工号 */
    @Excel(name = "工号")
    private String jobNumber;


    /** 拓展属性 */
    private String extattr;

    /** 离职标志 */
    private String canDimission;

    /** 修改标识 */
    private String updateFlag;

    /** 删除标识 */
    private String delFlag;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 公司类型(0:自动化,1:智能,2:潍坊，3：宝仓) */
    private Integer companyType;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 唯一id */
    private String dataId;

    /** 主部门 */
    //@Excel(name = "主部门")
    private Long mainDept;

    /** 身份证 */
    //@Excel(name = "身份证")
    private String cardNum;

    /** 人员类型 0:实习生，1:外包 */
    @Excel(name = "人员类型",dictType="emp_type")
    private Integer empType;

    /** 入职时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职日期", dateFormat = "yyyy-MM-dd")
    private Date hiredDate;

    /** 离职日期 */
    @Excel(name = "离职日期",dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastWorkDay ;

}
