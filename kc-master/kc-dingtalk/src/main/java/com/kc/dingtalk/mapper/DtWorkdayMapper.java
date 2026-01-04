package com.kc.dingtalk.mapper;


import com.kc.common.core.domain.entity.DtWorkday;

import java.util.List;

/**
 * 员工考勤日期Mapper接口
 *
 * @author zhaobo.yang
 * @date 2023-10-18
 */
public interface DtWorkdayMapper
{
    /**
     * 查询员工考勤日期
     *
     * @param id 员工考勤日期主键
     * @return 员工考勤日期
     */
    public DtWorkday selectDtWorkdayById(Long id);

    /**
     * 查询员工考勤日期列表
     *
     * @param dtWorkday 员工考勤日期
     * @return 员工考勤日期集合
     */
    public List<DtWorkday> selectDtWorkdayList(DtWorkday dtWorkday);

    /**
     * 新增员工考勤日期
     *
     * @param dtWorkday 员工考勤日期
     * @return 结果
     */
    public int insertDtWorkday(DtWorkday dtWorkday);

    /**
     * 修改员工考勤日期
     *
     * @param dtWorkday 员工考勤日期
     * @return 结果
     */
    public int updateDtWorkday(DtWorkday dtWorkday);

    /**
     * 删除员工考勤日期
     *
     * @param id 员工考勤日期主键
     * @return 结果
     */
    public int deleteDtWorkdayById(Long id);

    /**
     * 批量删除员工考勤日期
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDtWorkdayByIds(Long[] ids);

    /**
     * 批量新增
     *
     * @param list
     * @return 结果
     */
    public int batchInsert(List<DtWorkday> list);
}
