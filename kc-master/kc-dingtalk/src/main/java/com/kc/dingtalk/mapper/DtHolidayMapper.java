package com.kc.dingtalk.mapper;

import com.kc.common.core.domain.dao.EmployeeSaveDao;
import com.kc.common.core.domain.entity.DtHoliday;

import java.util.List;

/**
 * 节假日Mapper接口
 *
 * @author kc
 * @date 2024-04-30
 */
public interface DtHolidayMapper
{
    /**
     * 查询节假日
     *
     * @param id 节假日主键
     * @return 节假日
     */
    public DtHoliday selectDtHolidayById(Long id);

    /**
     * 查询节假日列表
     *
     * @param dtHoliday 节假日
     * @return 节假日集合
     */
    public List<DtHoliday> selectDtHolidayList(DtHoliday dtHoliday);

    /**
     * 新增节假日
     *
     * @param dtHoliday 节假日
     * @return 结果
     */
    public int insertDtHoliday(DtHoliday dtHoliday);

    /**
     * 修改节假日
     *
     * @param dtHoliday 节假日
     * @return 结果
     */
    public int updateDtHoliday(DtHoliday dtHoliday);

    /**
     * 删除节假日
     *
     * @param id 节假日主键
     * @return 结果
     */
    public int deleteDtHolidayById(Long id);

    /**
     * 批量删除节假日
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDtHolidayByIds(Long[] ids);


    /**
     * 批量新增
     *
     * @param holidays 节假日列表
     * @return 结果
     */
    int batchInsert(List<DtHoliday> holidays);

    /**
     * 根据日期查询节假日信息
     *
     * @param curDay 日期（格式：yyyyMMdd）
     * @return 节假日信息
     */
    DtHoliday selectDtHolidayByCurDay(String curDay);
}
