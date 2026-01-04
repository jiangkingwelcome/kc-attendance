package com.kc.dingtalk.mapper;


import com.kc.common.core.domain.entity.DtOvertime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工加班记录Mapper接口
 *
 * @author zhaobo.yang
 * @date 2023-10-19
 */
public interface DtOvertimeMapper
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
     * 删除员工加班记录
     *
     * @param id 员工加班记录主键
     * @return 结果
     */
    public int deleteDtOvertimeById(Long id);

    /**
     * 批量删除员工加班记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDtOvertimeByIds(Long[] ids);

    /**
     * @description: 批量插入
     * @author: zhaobo.yang
     * @date: 2023/10/19 11:40
     */
    int batchInsert(List<DtOvertime> list);

    /**
     * @description:  根据批次号删除数据
     * @param batchNoStart 日期批次号开始
     * @param batchNoEnd 日期批次号结束
     * @author: zhaobo.yang
     * @date: 2022/12/2 10:33
     */
    int deleteByBatchNoRange(@Param("batchNoStart") String batchNoStart,
                             @Param("batchNoEnd") String batchNoEnd,
                             @Param("employeeIds")List<String> employeeIds,
                             @Param("companyType")Integer companyType);
}
