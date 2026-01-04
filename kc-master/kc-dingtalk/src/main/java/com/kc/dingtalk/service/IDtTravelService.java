package com.kc.dingtalk.service;

import com.kc.common.core.domain.entity.DtTravel;

import java.util.Date;
import java.util.List;


/**
 * 外出记录Service接口
 *
 * @author kc
 * @date 2024-05-06
 */
public interface IDtTravelService
{
    /**
     * 查询外出记录
     *
     * @param id 外出记录主键
     * @return 外出记录
     */
    public DtTravel selectDtTravelById(Long id);

    /**
     * 查询外出记录列表
     *
     * @param dtTravel 外出记录
     * @return 外出记录集合
     */
    public List<DtTravel> selectDtTravelList(DtTravel dtTravel);

    /**
     * 新增外出记录
     *
     * @param dtTravel 外出记录
     * @return 结果
     */
    public int insertDtTravel(DtTravel dtTravel);

    /**
     * 修改外出记录
     *
     * @param dtTravel 外出记录
     * @return 结果
     */
    public int updateDtTravel(DtTravel dtTravel);

    /**
     * 批量删除外出记录
     *
     * @param ids 需要删除的外出记录主键集合
     * @return 结果
     */
    public int deleteDtTravelByIds(Long[] ids);

    /**
     * 删除外出记录信息
     *
     * @param id 外出记录主键
     * @return 结果
     */
    public int deleteDtTravelById(Long id);

    /**
     * 批量插入
     * @param dtTravels
     */
    public int batchInsert(List<DtTravel> dtTravels);

    /**
     * 使用汇联易进行同步差旅申请
     *
     * @param startDate 最后修改时间开始时间
     * @param endDate 最后修改时间结束时间
     * 该方法用于通过汇联易系统进行差旅申请的同步操作。传入差旅的开始和结束日期，系统将根据这些信息
     * 自动填写和提交差旅申请。无需返回值，操作完成后系统将直接返回申请结果。
     */
    void synTravelByHuiLianYi(Date startDate, Date endDate);

}
