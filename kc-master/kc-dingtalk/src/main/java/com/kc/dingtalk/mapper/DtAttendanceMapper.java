package com.kc.dingtalk.mapper;

import java.util.List;

import com.kc.common.core.domain.dao.AttendanceDao;
import com.kc.common.core.domain.dao.GetListByParamsResults;
import com.kc.common.core.domain.entity.DtAttendance;
import org.apache.ibatis.annotations.Param;

/**
 * 员工考勤记录Mapper接口
 *
 * @author zhaobo.yang
 * @date 2022-11-07
 */
public interface DtAttendanceMapper
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
     * 根据唯一标识修改员工考勤记录
     *
     * @param dtAttendance 员工考勤记录
     * @return 结果
     */
    int updateAttendanceByDataId(DtAttendance dtAttendance);

    /**
     * 删除员工考勤记录
     *
     * @param id 员工考勤记录主键
     * @return 结果
     */
    int deleteDtAttendanceById(Long id);

    /**
     * 批量删除员工考勤记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDtAttendanceByIds(Long[] ids);

    /**
     * 删除员工考勤记录
     *
     * @return 结果
     */
     int batchDeleteByDataIds(List<String> list);

    /**
     * @description:  查询钉钉用户考勤记录
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/8 15:07
     */
    List<GetListByParamsResults> getListByParams(AttendanceDao attendanceDao);

    /**
     * @description:  根据批次号删除数据
     * @param batchNoStart 日期批次号开始
     * @param batchNoEnd 日期批次号结束
     * @author: zhaobo.yang
     * @date: 2022/12/2 10:33
     */
    void deleteByBatchNoRange(@Param("batchNoStart") String batchNoStart,@Param("batchNoEnd") String batchNoEnd);

    /**
     * 新增员工考勤记录
     *
     * @param dtAttendances 员工考勤记录集合
     * @return 结果
     */
    int batchInsertDtAttendance(List<DtAttendance> dtAttendances);
}
