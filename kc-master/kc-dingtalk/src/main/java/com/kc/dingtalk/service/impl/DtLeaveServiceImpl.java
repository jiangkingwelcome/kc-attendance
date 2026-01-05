package com.kc.dingtalk.service.impl;

import java.util.List;
import com.kc.common.core.domain.entity.DtLeave;
import com.kc.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kc.dingtalk.mapper.DtLeaveMapper;
import com.kc.dingtalk.service.IDtLeaveService;

/**
 * 员工请假审批单Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2023-09-26
 */
@Service
public class DtLeaveServiceImpl implements IDtLeaveService
{
    @Autowired
    private DtLeaveMapper dtLeaveMapper;

    /**
     * 查询员工请假记录
     *
     * @param id 员工请假记录主键
     * @return 员工请假记录
     */
    @Override
    public DtLeave selectDtLeaveById(Long id)
    {
        return dtLeaveMapper.selectDtLeaveById(id);
    }

    /**
     * 查询员工请假记录列表
     *
     * @param dtLeave 员工请假记录
     * @return 员工请假记录
     */
    @Override
    public List<DtLeave> selectDtLeaveList(DtLeave dtLeave)
    {
        return dtLeaveMapper.selectDtLeaveList(dtLeave);
    }

    /**
     * 新增员工请假记录
     *
     * @param dtLeave 员工请假记录
     * @return 结果
     */
    @Override
    public int insertDtLeave(DtLeave dtLeave)
    {
        dtLeave.setCreateTime(DateUtils.getNowDate());
        return dtLeaveMapper.insertDtLeave(dtLeave);
    }

    /**
     * 修改员工请假记录
     *
     * @param dtLeave 员工请假记录
     * @return 结果
     */
    @Override
    public int updateDtLeave(DtLeave dtLeave)
    {
        dtLeave.setUpdateTime(DateUtils.getNowDate());
        return dtLeaveMapper.updateDtLeave(dtLeave);
    }

    /**
     * 批量删除员工请假记录
     *
     * @param ids 需要删除的员工请假记录主键
     * @return 结果
     */
    @Override
    public int deleteDtLeaveByIds(Long[] ids)
    {
        return dtLeaveMapper.deleteDtLeaveByIds(ids);
    }

    /**
     * 删除员工请假记录信息
     *
     * @param id 员工请假记录主键
     * @return 结果
     */
    @Override
    public int deleteDtLeaveById(Long id)
    {
        return dtLeaveMapper.deleteDtLeaveById(id);
    }

    @Override
    public int batchInsert(List<DtLeave> leaves) {
        return dtLeaveMapper.batchInsert(leaves);
    }

    @Override
    public void deleteByBatchNoRange(String batchNoStart, String batchNoEnd,List<String> employeeIds,Integer companyType) {
         dtLeaveMapper.deleteByBatchNoRange(batchNoStart,batchNoEnd,employeeIds,companyType);
    }

    @Override
    public int getTodayLeaveCount() {
        return dtLeaveMapper.getTodayLeaveCount();
    }
}
