package com.kc.dingtalk.service;

import java.util.List;

import com.kc.common.core.domain.dao.AttendanceDao;
import com.kc.common.core.domain.entity.DtAttendance;

/**
 * 员工考勤记录Service接口
 *
 * @author zhaobo.yang
 * @date 2022-11-07
 */
public interface IDtAttendanceService
{
    /**
     * 查询员工考勤记录
     *
     * @param id 员工考勤记录主键
     * @return 员工考勤记录
     */
     DtAttendance selectDtAttendanceById(Long id);

    /**
     * 查询员工考勤记录列表
     *
     * @param attendanceDao 员工考勤记录
     * @return 员工考勤记录集合
     */
     List<DtAttendance> selectDtAttendanceList(AttendanceDao attendanceDao);

    /**
     * 新增员工考勤记录
     *
     * @param dtAttendance 员工考勤记录
     * @return 结果
     */
     int insertDtAttendance(DtAttendance dtAttendance);

    /**
     * 修改员工考勤记录
     *
     * @param dtAttendance 员工考勤记录
     * @return 结果
     */
     int updateDtAttendance(DtAttendance dtAttendance);

    /**
     * 批量删除员工考勤记录
     *
     * @param ids 需要删除的员工考勤记录主键集合
     * @return 结果
     */
     int deleteDtAttendanceByIds(Long[] ids);

    /**
     * 删除员工考勤记录信息
     *
     * @param id 员工考勤记录主键
     * @return 结果
     */
     int deleteDtAttendanceById(Long id);


    /**
     * 根据batchNo删除考勤数据
     *
     * @param batchNo 时间批次
     * @return 结果
     */
    void deleteByBatchNo(String batchNo);

    /**
     * @description: 批量插入考勤数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/7 10:59
     */
    void batchInsertDtAttendance(List<DtAttendance> dtAttendances);

    /**
     * 批量删除员工考勤记录
     */
    int batchDeleteByDataIds(List<String> list);
}
