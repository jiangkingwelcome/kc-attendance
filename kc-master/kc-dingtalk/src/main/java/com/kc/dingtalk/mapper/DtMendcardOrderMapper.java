package com.kc.dingtalk.mapper;

import com.kc.common.core.domain.dao.DtMendcardOrder;
import java.util.List;


/**
 * 钉钉补卡工单Mapper接口
 *
 * @author zhaobo.yang
 * @date 2023-07-04
 */
public interface DtMendcardOrderMapper {
    /**
     * 查询钉钉补卡工单
     *
     * @param id 钉钉补卡工单主键
     * @return 钉钉补卡工单
     */
    public DtMendcardOrder selectDtMendcardOrderById(Long id);

    /**
     * 查询钉钉补卡工单列表
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 钉钉补卡工单集合
     */
    public List<DtMendcardOrder> selectDtMendcardOrderList(DtMendcardOrder dtMendcardOrder);

    /**
     * 查询钉钉补卡工单列表
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 钉钉补卡工单集合
     */
    public List<DtMendcardOrder> queryByInstanceIds(DtMendcardOrder dtMendcardOrder);

    /**
     * 新增钉钉补卡工单
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 结果
     */
    public int insertDtMendcardOrder(DtMendcardOrder dtMendcardOrder);

    /**
     * 批量新增
     *
     * @param dtMendcardOrders 钉钉补卡工单
     * @return 结果
     */
    int batchInsert(List<DtMendcardOrder> dtMendcardOrders);

    /**
     * 修改钉钉补卡工单
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 结果
     */
    public int updateDtMendcardOrder(DtMendcardOrder dtMendcardOrder);

    /**
     * 删除钉钉补卡工单
     *
     * @param id 钉钉补卡工单主键
     * @return 结果
     */
    public int deleteDtMendcardOrderById(Long id);

    /**
     * 批量删除钉钉补卡工单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDtMendcardOrderByIds(Long[] ids);


    /**
     * @param dtMendcardOrder 钉钉补卡工单
     * @description: 根据工单实例ids批量修改
     * @author: zhaobo.yang
     * @date: 2023/7/7 13:43
     */

    int updateDtMendcardOrderByInstanceIds(DtMendcardOrder dtMendcardOrder);
}
