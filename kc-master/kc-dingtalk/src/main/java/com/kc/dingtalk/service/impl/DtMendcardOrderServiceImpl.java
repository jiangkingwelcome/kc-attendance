package com.kc.dingtalk.service.impl;

import com.kc.common.core.domain.dao.DtMendcardOrder;
import com.kc.common.utils.DateUtils;
import com.kc.dingtalk.mapper.DtMendcardOrderMapper;
import com.kc.dingtalk.service.IDtMendcardOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * 钉钉补卡工单Service业务层处理
 *
 * @author kc
 * @date 2023-07-03
 */
@Service
public class DtMendcardOrderServiceImpl implements IDtMendcardOrderService {
    @Autowired
    private DtMendcardOrderMapper dtMendcardOrderMapper;

    /**
     * 查询钉钉补卡工单
     *
     * @param id 钉钉补卡工单主键
     * @return 钉钉补卡工单
     */
    @Override
    public DtMendcardOrder selectDtMendcardOrderById(Long id) {
        return dtMendcardOrderMapper.selectDtMendcardOrderById(id);
    }

    /**
     * 查询钉钉补卡工单列表
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 钉钉补卡工单
     */
    @Override
    public List<DtMendcardOrder> selectDtMendcardOrderList(DtMendcardOrder dtMendcardOrder) {
        return dtMendcardOrderMapper.selectDtMendcardOrderList(dtMendcardOrder);
    }

    /**
     * 新增钉钉补卡工单
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 结果
     */
    @Override
    public int insertDtMendcardOrder(DtMendcardOrder dtMendcardOrder) {
        dtMendcardOrder.setCreateTime(DateUtils.getNowDate());
        return dtMendcardOrderMapper.insertDtMendcardOrder(dtMendcardOrder);
    }

    /**
     * 修改钉钉补卡工单
     *
     * @param dtMendcardOrder 钉钉补卡工单
     * @return 结果
     */
    @Override
    public int updateDtMendcardOrder(DtMendcardOrder dtMendcardOrder) {
        dtMendcardOrder.setUpdateTime(DateUtils.getNowDate());
        return dtMendcardOrderMapper.updateDtMendcardOrder(dtMendcardOrder);
    }

    /**
     * 批量删除钉钉补卡工单
     *
     * @param ids 需要删除的钉钉补卡工单主键
     * @return 结果
     */
    @Override
    public int deleteDtMendcardOrderByIds(Long[] ids) {
        return dtMendcardOrderMapper.deleteDtMendcardOrderByIds(ids);
    }

    /**
     * 删除钉钉补卡工单信息
     *
     * @param id 钉钉补卡工单主键
     * @return 结果
     */
    @Override
    public int deleteDtMendcardOrderById(Long id) {
        return dtMendcardOrderMapper.deleteDtMendcardOrderById(id);
    }

    @Override
    public int batchInsert(List<DtMendcardOrder> mendcardOrders) {
        return dtMendcardOrderMapper.batchInsert(mendcardOrders);
    }

    @Override
    public int updateDtMendcardOrderByInstanceIds(DtMendcardOrder dtMendcardOrder) {
        if(dtMendcardOrder == null || CollectionUtils.isEmpty(dtMendcardOrder.getInstanceIds())){
            return 0;
        }
        return dtMendcardOrderMapper.updateDtMendcardOrderByInstanceIds(dtMendcardOrder);
    }

}
