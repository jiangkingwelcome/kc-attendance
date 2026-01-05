package com.kc.dingtalk.mapper;

import com.kc.common.core.domain.entity.DtLeave;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工请假审批单Mapper接口
 *
 * @author zhaobo.yang
 * @date 2023-09-26
 */
public interface DtLeaveMapper
{
    /**
     * 查询员工请假记录
     *
     * @param id 员工请假记录主键
     * @return 员工请假记录
     */
    public DtLeave selectDtLeaveById(Long id);

    /**
     * 查询员工请假记录列表
     *
     * @param dtLeave 员工请假记录
     * @return 员工请假记录集合
     */
    public List<DtLeave> selectDtLeaveList(DtLeave dtLeave);

    /**
     * 新增员工请假记录
     *
     * @param dtLeave 员工请假记录
     * @return 结果
     */
    public int insertDtLeave(DtLeave dtLeave);

    /**
     * 修改员工请假记录
     *
     * @param dtLeave 员工请假记录
     * @return 结果
     */
    public int updateDtLeave(DtLeave dtLeave);

    /**
     * 删除员工请假记录
     *
     * @param id 员工请假记录主键
     * @return 结果
     */
    public int deleteDtLeaveById(Long id);

    /**
     * 批量删除员工请假记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDtLeaveByIds(Long[] ids);

    /**
     * 批量新增
     *
     * @param dtLeaves 钉钉请假工单
     * @return 结果
     */
    int batchInsert(List<DtLeave> dtLeaves);

    /**
     * @description:  根据批次号删除数据
     * @param batchNoStart 日期批次号开始
     * @param batchNoEnd 日期批次号结束
     * @author: zhaobo.yang
     * @date: 2022/12/2 10:33
     */
    void deleteByBatchNoRange(@Param("batchNoStart") String batchNoStart,
                              @Param("batchNoEnd") String batchNoEnd,
                              @Param("employeeIds")List<String> employeeIds,
                              @Param("companyType")Integer companyType
    );

    /**
     * 查询今日请假人数
     *
     * @return 今日请假人数
     */
    int getTodayLeaveCount();
}
