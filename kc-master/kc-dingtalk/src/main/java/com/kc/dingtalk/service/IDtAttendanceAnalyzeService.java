package com.kc.dingtalk.service;

import java.util.List;

import com.kc.common.core.domain.dao.AttendanceAnalyzeDao;
import com.kc.common.core.domain.entity.DtAttendanceAnalyze;

/**
 * 员工考勤汇总分析Service接口
 *
 * @author zhaobo.yang
 * @date 2022-11-08
 */
public interface IDtAttendanceAnalyzeService
{
    /**
     * 查询员工考勤汇总分析
     *
     * @param id 员工考勤汇总分析主键
     * @return 员工考勤汇总分析
     */
    public DtAttendanceAnalyze selectDtAttendanceAnalyzeById(Long id);

    /**
     * 查询员工考勤汇总分析列表
     *
     * @param attendanceAnalyzeDao 员工考勤汇总分析
     * @return 员工考勤汇总分析集合
     */
     List<DtAttendanceAnalyze> selectDtAttendanceAnalyzeList(AttendanceAnalyzeDao attendanceAnalyzeDao);

    /**
     * 新增员工考勤汇总分析
     *
     * @param dtAttendanceAnalyze 员工考勤汇总分析
     * @return 结果
     */
    public int insertDtAttendanceAnalyze(DtAttendanceAnalyze dtAttendanceAnalyze);

    /**
     * 修改员工考勤汇总分析
     *
     * @param dtAttendanceAnalyze 员工考勤汇总分析
     * @return 结果
     */
    public int updateDtAttendanceAnalyze(DtAttendanceAnalyze dtAttendanceAnalyze);

    /**
     * 批量删除员工考勤汇总分析
     *
     * @param ids 需要删除的员工考勤汇总分析主键集合
     * @return 结果
     */
    public int deleteDtAttendanceAnalyzeByIds(Long[] ids);

    /**
     * 删除员工考勤汇总分析信息
     *
     * @param id 员工考勤汇总分析主键
     * @return 结果
     */
    public int deleteDtAttendanceAnalyzeById(Long id);

    /**
     * 删除时间之前的考勤和签到数据
     *
     * @param
     */
    void deleteAttendanceAnalyze(String batchNo);
}
