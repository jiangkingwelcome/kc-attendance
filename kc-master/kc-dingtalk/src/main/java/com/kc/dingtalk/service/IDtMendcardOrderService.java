package com.kc.dingtalk.service;

import com.kc.common.core.domain.dao.DtMendcardOrder;
import java.util.List;

/**
 * 钉钉补卡工单Service接口
 *
 * @author kc
 * @date 2023-07-03
 */
public interface IDtMendcardOrderService
{
    /**
     * 查询钉钉补卡工单
     *
     * @param id 钉钉补卡工单主键
     * @return 钉钉补卡工单
     */
     DtMendcardOrder selectDtMendcardOrderById(Long id);

    /**
     * 查询钉钉补卡工单列表
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 钉钉补卡工单集合
     */
     List<DtMendcardOrder> selectDtMendcardOrderList(DtMendcardOrder dtMendcardOrder);

    /**
     * 新增钉钉补卡工单
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 结果
     */
     int insertDtMendcardOrder(DtMendcardOrder dtMendcardOrder);

    /**
     * 修改钉钉补卡工单
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 结果
     */
     int updateDtMendcardOrder(DtMendcardOrder dtMendcardOrder);

    /**
     * 批量删除钉钉补卡工单
     *
     * @param ids 需要删除的钉钉补卡工单主键集合
     * @return 结果
     */
     int deleteDtMendcardOrderByIds(Long[] ids);

    /**
     * 删除钉钉补卡工单信息
     *
     * @param id 钉钉补卡工单主键
     * @return 结果
     */
     int deleteDtMendcardOrderById(Long id);

    /**
     * @description:  批量入库
     * @param mendcardOrders
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/4 18:41
     */
    int batchInsert(List<DtMendcardOrder> mendcardOrders);


    /**
     * @description:  批量修改
     * @param dtMendcardOrder
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/4 18:41
     */
    int updateDtMendcardOrderByInstanceIds(DtMendcardOrder dtMendcardOrder);
}
