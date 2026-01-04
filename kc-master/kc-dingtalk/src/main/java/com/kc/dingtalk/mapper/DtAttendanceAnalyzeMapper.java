package com.kc.dingtalk.mapper;

import java.util.List;

import com.kc.common.core.domain.dao.AttendanceAnalyzeDao;
import com.kc.common.core.domain.entity.DtAttendanceAnalyze;
import org.apache.ibatis.annotations.Param;

/**
 * 员工考勤汇总分析Mapper接口
 *
 * @author zhaobo.yang
 * @date 2022-11-08
 */
public interface DtAttendanceAnalyzeMapper
{
    /**
     * 查询员工考勤汇总分析
     *
     * @param id 员工考勤汇总分析主键
     * @return 员工考勤汇总分析
     */
    DtAttendanceAnalyze selectDtAttendanceAnalyzeById(Long id);

    /**
     * 查询员工考勤汇总分析列表
     *
     * @param query 员工考勤汇总分析查询类
     * @return 员工考勤汇总分析集合
     */
    List<DtAttendanceAnalyze> selectDtAttendanceAnalyzeList(AttendanceAnalyzeDao query);

    /**
     * 新增员工考勤汇总分析
     *
     * @param dtAttendanceAnalyze 员工考勤汇总分析
     * @return 结果
     */
    int insertDtAttendanceAnalyze(DtAttendanceAnalyze dtAttendanceAnalyze);

    /**
     * 批量新增员工考勤汇总分析
     *
     * @param dtAttendanceAnalyzes 员工考勤汇总分析集合
     * @return
     */
     int batchInsertDtAttendanceAnalyze(List<DtAttendanceAnalyze> dtAttendanceAnalyzes);

    /**
     * 修改员工考勤汇总分析
     *
     * @param dtAttendanceAnalyze 员工考勤汇总分析
     * @return 结果
     */
    int updateDtAttendanceAnalyze(DtAttendanceAnalyze dtAttendanceAnalyze);

    /**
     * 删除员工考勤汇总分析
     *
     * @param id 员工考勤汇总分析主键
     * @return 结果
     */
    int deleteDtAttendanceAnalyzeById(Long id);

    /**
     * 批量删除员工考勤汇总分析
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDtAttendanceAnalyzeByIds(Long[] ids);

    /**
     * @description:  根据批次号删除数据
     * @param batchNoStart 日期批次号开始
     * @param batchNoEnd 日期批次号结束
     * @author: zhaobo.yang
     * @date: 2022/12/2 10:33
     */
    void deleteByBatchNoRange(@Param("batchNoStart") String batchNoStart,@Param("batchNoEnd") String batchNoEnd);


}
