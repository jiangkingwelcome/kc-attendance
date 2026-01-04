package com.kc.dingtalk.service;


import com.kc.common.core.domain.entity.DtOvertime;

import java.util.Date;
import java.util.List;

/**
 * 员工加班记录Service接口
 *
 * @author zhaobo.yang
 * @date 2023-10-19
 */
public interface IDtOvertimeService
{
    /**
     * 查询员工加班记录
     *
     * @param id 员工加班记录主键
     * @return 员工加班记录
     */
    public DtOvertime selectDtOvertimeById(Long id);

    /**
     * 查询员工加班记录列表
     *
     * @param dtOvertime 员工加班记录
     * @return 员工加班记录集合
     */
    public List<DtOvertime> selectDtOvertimeList(DtOvertime dtOvertime);

    /**
     * 新增员工加班记录
     *
     * @param dtOvertime 员工加班记录
     * @return 结果
     */
    public int insertDtOvertime(DtOvertime dtOvertime);

    /**
     * 修改员工加班记录
     *
     * @param dtOvertime 员工加班记录
     * @return 结果
     */
    public int updateDtOvertime(DtOvertime dtOvertime);

    /**
     * 批量删除员工加班记录
     *
     * @param ids 需要删除的员工加班记录主键集合
     * @return 结果
     */
    public int deleteDtOvertimeByIds(Long[] ids);

    /**
     * 删除员工加班记录信息
     *
     * @param id 员工加班记录主键
     * @return 结果
     */
    public int deleteDtOvertimeById(Long id);

    /**
     * 批量新增
     * @return 结果
     */
    public int batchInsert(List<DtOvertime> list);

    /**
     * 从钉钉同步加班记录
     * @return 结果
     */
    public void synGetOverTimeList(Date startTime, Date endTime,Integer companyType);


    /**
     * 根据batchNo删除数据
     *
     * @return 结果
     */
    public int deleteByBatchNoRange(String batchNoStart,String batchNoEnd,List<String> employeeIds,Integer companyType);
}
