package com.kc.dingtalk.mapper;


import com.kc.common.core.domain.entity.DtLeave;
import com.kc.common.core.domain.entity.DtTravel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 外出记录Mapper接口
 *
 * @author kc
 * @date 2024-05-06
 */
public interface DtTravelMapper
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
     * 删除外出记录
     *
     * @param id 外出记录主键
     * @return 结果
     */
    public int deleteDtTravelById(Long id);

    /**
     * 批量删除外出记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDtTravelByIds(Long[] ids);


    /**
     * 批量插入旅行记录到数据库。
     *
     * @param dtTravels 包含待插入旅行记录的列表。每个旅行记录都应包含完整的数据，以确保插入成功。
     * @return 返回插入的记录数。如果所有记录都成功插入，返回值应等于传入列表的大小。
     */
    int batchInsert(List<DtTravel> dtTravels);


    /**
     * 根据业务代码列表删除相关数据。
     *
     * @param businessCodes 业务代码列表，用于指定需要删除的数据的业务标识。
     *                      通过这些业务代码，可以定位到多个相关联的数据项。
     * @return 无返回值
     */
    void deleteByBusinessCodes(@Param("businessCodes") List<String> businessCodes);


}
