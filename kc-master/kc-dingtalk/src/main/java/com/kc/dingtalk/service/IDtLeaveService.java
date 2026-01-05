package com.kc.dingtalk.service;

import com.kc.common.core.domain.entity.DtLeave;
import java.util.List;

/**
 * 员工请假审批单Service接口
 *
 * @author zhaobo.yang
 * @date 2023-09-26
 */
public interface IDtLeaveService
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
     * 批量删除员工请假记录
     *
     * @param ids 需要删除的员工请假记录主键集合
     * @return 结果
     */
    public int deleteDtLeaveByIds(Long[] ids);

    /**
     * 删除员工请假记录信息
     *
     * @param id 员工请假记录主键
     * @return 结果
     */
    public int deleteDtLeaveById(Long id);

    /**
     * @description:  请假工单批量入库
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/9/27 17:48
     */
    int batchInsert(List<DtLeave> leaves);


    /**
     * @description:  根据批次号删除数据
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/10/10 10:01
     */
    void deleteByBatchNoRange(String batchNoStart,String batchNoEnd,List<String> employeeIds,Integer companyType);

    /**
     * 查询今日请假人数
     *
     * @return 今日请假人数
     */
    int getTodayLeaveCount();
}
