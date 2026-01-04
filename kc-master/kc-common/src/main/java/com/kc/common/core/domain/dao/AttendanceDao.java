package com.kc.common.core.domain.dao;

import com.kc.common.annotation.Excel;
import com.kc.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 员工考勤记录对象 dt_attendance
 *
 * @author zhaobo.yang
 * @date 2022-12-02
 */

@Data
public class AttendanceDao extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 主键ids */
    private List<Long> ids;

    /** 员工id */
    private String employeeId;

    /** 员工名称 */
    private String name;

    /** 员工打卡时间（时间戳） */
    private String userCheckTimeStamp;

    /** 员工打卡时间（时间戳）开始 */
    private String startUserCheckTimeStamp;

    /** 员工打卡时间（时间戳）结束 */
    private String endUserCheckTimeStamp;

    /** 员工打卡时间 */
    private Date userCheckTime;

    /** 日期批次 */
    private String batchNo;

    /** 开始日期批次 */
    private String beginBatchNo;

    /** 结束日期批次 */
    private String endBatchNo;
    /** 类型(0:钉钉打卡,1:钉钉签到，2：补卡,3:请假) */
    private Integer type;

    /** 公司类型(0:自动化,1:智能,2:潍坊，3:宝仓) */
    private Integer companyType;

    /** 数据类型(0:钉钉同步,1:新增,2:修改) */
    private Integer dataType;

    /** 人员ids */
    private List<String> employeeIds;

    /** 唯一标识集合 */
    private List<String> dataIds;

    /** 唯一标识 */
    private String dataId;

    /** 部门id */
    private Long deptId;

    /** 签到地址 */
    private String place;

    /** 删除标志 */
    private String delFlag;
}
