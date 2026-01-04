package com.kc.common.core.domain.dao;

import com.kc.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 员工考勤记录对象 dt_attendance
 *
 * @author zhaobo.yang
 * @date 2022-12-02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetListByParamsResults extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 员工id */
    private String userId;

    /** 员工名称 */
    private String name;

    /** 工号 */
    private String jobNumber;
    /** 记录条数 */
    private Integer attendanceCount;

    /** 上班时间 */
    private Date workTime;

    /** 下班时间 */
    private Date offWorkTime;

    /** 日期批次 */
    private String batchNo;

    /** 打卡、签到地址 */
    private String place;

    /** 请假记录 */
    private String leave;

    /** 请假时长(天)*/
    private String leaveDuration;

    /** 请假次数 */
    private String leaveCount;

    /** 出差记录 */
    private String travel;

    /** 出差时长(小时)*/
    private String travelDuration;

    /** 出差次数 */
    private String travelCount;

    /** 加班类型 */
    private Integer overtimeType;

    /** 加班时间(天)*/
    private String overtimeDurationDay;

    /** 加班时间(小时)*/
    private String overtimeDurationHour;

    /** 人员类型*/
    private Integer empType;
}
