package com.kc.dingtalk.dingding;

import com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsResponseBody;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import com.dingtalk.api.response.OapiAttendanceGetcolumnvalResponse;
import com.dingtalk.api.response.OapiAttendanceGetupdatedataResponse;
import com.dingtalk.api.response.OapiAttendanceListRecordResponse;
import com.dingtalk.api.response.OapiCheckinRecordResponse;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeV2ListResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.dingtalk.response.DtDeptResponse;
import java.util.Date;
import java.util.List;

/**
 * 钉钉部门Service接口
 *
 * @author kc
 * @date 2022-11-03
 */
public interface DingTalkService {
    /**
     * @description: 获取钉钉token
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/4 11:09
     */
    String getToken(int companyType);

    /**
     * 获取指定用户的考勤记录。
     *
     * @param userIds 用户ID列表，指定要查询考勤记录的用户。
     * @param begin 开始时间，用于查询考勤记录的起始日期。
     * @param end 结束时间，用于查询考勤记录的结束日期。
     * @param companyType 公司类型，用于指定查询的公司范围。
     * @return 返回一个包含考勤记录的列表，每个记录都是OapiAttendanceListRecordResponse.Recordresult类型。
     */

    List<OapiAttendanceListRecordResponse.Recordresult> getAttendances(List<String> userIds, String begin, String end, int companyType);

    /**
     * 根据部门ID获取签到记录
     *
     * @param deptId 部门ID，用于查询指定部门的签到记录
     * @param offset 查询起始偏移量，用于分页查询
     * @param size 查询每页记录数，用于分页查询
     * @param begin 查询开始时间，格式为日期字符串
     * @param end 查询结束时间，格式为日期字符串
     * @param companyType 公司类型，用于指定查询的公司范围
     * @return 返回签到记录列表，包含签到的详细信息
     */
    List<OapiCheckinRecordResponse.Data> getSignInRecordsByDeptId(String deptId, Long offset, Long size, String begin, String end, int companyType);


    /**
     * @description: 获取钉钉补卡工单实例ids
     * @author: zhaobo.yang
     * @date: 2023/9/27 11:05
     */
    List<String> getWorkOrderInstanceIds(Long nextToken, Long startTimeStamp, Long endTimeStamp, String processCode);

    /**
     * @param instanceId 工单实例id
     * @description: 根据工单id查询工单详情
     * @author: zhaobo.yang
     * @date: 2023/9/27 11:05
     */
    GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult getWorkOrderDetail(String instanceId);

    /**
     * @param userId    员工id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @description: 获取报表中的加班信息
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/10/7 15:53
     */
    List<OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal> listOverTime(String userId, Integer companyType, Date startTime, Date endTime);

    /**
     * 根据父部门id获取子部门列表
     *
     * @param parentId
     * @param companyType
     * @return
     */
    List<DtDeptResponse> listDeptByParentId(Long parentId, int companyType);


    /**
     * 获取指定公司类型的根部门
     *
     * @param companyType 公司类型的标识，用于指定要查询的公司的类型
     * @return SysDept 返回根部门的对象，如果找不到对应的根部门，则返回null
     */
    SysDept getRootDept(int companyType);
    /**
     * @description: 根据部门id获取用户列表
     * @return:
     * @author: zhaobo.yang
     * @date: 2024/4/25 18:50
     */
    List<OapiV2UserListResponse.ListUserResponse> getUsersByDeptId(Long deptId, int companyType, Long nextCursor, Long size);

    /**
     * @description:  拉取请假数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2024/4/25 18:50
     */
    OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo getUpdatedata(String userId, Date workDate, Integer companyType);

    /**
     * @description:  获取员工主部门
     * @return:
     * @author: zhaobo.yang
     * @date: 2024/4/25 18:50
     */
    public List<OapiSmartworkHrmEmployeeV2ListResponse.EmpRosterFieldVo> getEmployeeRosterList(List<String> userId, Integer companyType,String fieldFilterList);

    /**
     * @description:  获取员工离职信息
     * @return:
     * @author: zhaobo.yang
     * @date: 2024/4/25 18:50
     */
    List<ListEmpLeaveRecordsResponseBody.ListEmpLeaveRecordsResponseBodyRecords> getEmployeeDismissionInfo(String startDate, String endDate, String nextToken,Integer companyType);
}
