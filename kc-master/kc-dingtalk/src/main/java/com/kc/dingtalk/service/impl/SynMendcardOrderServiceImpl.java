package com.kc.dingtalk.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import com.kc.common.core.domain.dao.DtMendcardOrder;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtAttendance;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.enums.AttendanceDataType;
import com.kc.common.enums.AttendanceType;
import com.kc.common.enums.CompanyType;
import com.kc.common.enums.HandleStatusEnum;
import com.kc.common.enums.ProcessCodeType;
import com.kc.common.enums.WorkOrderStatusEnum;
import com.kc.common.enums.YesOrNo;
import com.kc.common.utils.DateUtils;
import com.kc.dingtalk.dingding.DingTalkService;
import com.kc.dingtalk.mapper.DtEmployeeMapper;
import com.kc.dingtalk.service.IDtAttendanceService;
import com.kc.dingtalk.service.IDtMendcardOrderService;
import com.kc.dingtalk.service.ISynAttendanceAnalyzeService;
import com.kc.dingtalk.service.ISynMendcardOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2023/7/4 11:14
 */
@Service
public class SynMendcardOrderServiceImpl implements ISynMendcardOrderService {
    @Autowired
    private IDtMendcardOrderService iDtMendcardOrderService;
    @Autowired
    private DingTalkService dingTalkService;
    @Autowired
    private IDtAttendanceService iDtAttendanceService;

    @Autowired
    private DtEmployeeMapper employeeMapper;

    @Autowired
    private ISynAttendanceAnalyzeService iSynAttendanceAnalyzeService;

    @Override
    public void mendcardCompletion(Long startTimeStamp, Long endTimeStamp) {
        //1.获取类型为：补卡且状态为：完结的工单
        List<String> workOrderInstanceIds = dingTalkService.getWorkOrderInstanceIds(0L, startTimeStamp, endTimeStamp, ProcessCodeType.补卡.getInfo());
        List<String> existInstanceIds = new ArrayList<>();
        List<String> newInstanceIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workOrderInstanceIds)) {
            //查询本地已存在工单
            DtMendcardOrder dtMendcardOrder = new DtMendcardOrder();
            dtMendcardOrder.setInstanceIds(workOrderInstanceIds);
            List<DtMendcardOrder> dtMendcardOrders = iDtMendcardOrderService.selectDtMendcardOrderList(dtMendcardOrder);
            existInstanceIds = dtMendcardOrders.stream().map(DtMendcardOrder::getInstanceId).collect(Collectors.toList());
            //过滤已存在工单
            HashSet hs1 = new HashSet(workOrderInstanceIds);
            HashSet hs2 = new HashSet(existInstanceIds);
            hs1.removeAll(hs2);
            newInstanceIds = new ArrayList<String>();
            newInstanceIds.addAll(hs1);
        }

        //查询所有用户id
        EmployeeDao queryDao = new EmployeeDao();
        queryDao.setCanDimission(YesOrNo.否_0.getCode()); //查询未离职人员
        List<DtEmployee> employees = employeeMapper.selectDtEmployeeList(queryDao);
        if (CollectionUtils.isEmpty(employees)) {
            return;
        }
        Map<String,String> employeeIdAndName = new HashMap<>();
        //设置员工employeeId和name
        List<String> userIds = new ArrayList<>();
        for (DtEmployee e : employees) {
            if (!userIds.contains(e.getEmployeeId())) {
                userIds.add(e.getEmployeeId());
                employeeIdAndName.put(e.getEmployeeId(),e.getName());
            }
        }

        List<DtMendcardOrder> saves = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(newInstanceIds)) {
            //查询工单详情
            for (String instanceId : newInstanceIds) {
                GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult workOrderDetail = dingTalkService.getWorkOrderDetail(instanceId);
                if (workOrderDetail != null) {
                    String result = workOrderDetail.getResult();
                    if ("agree".equals(result)) {
                        DtMendcardOrder dtMendcardOrder = new DtMendcardOrder();
                        List<GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultFormComponentValues> formComponentValues = workOrderDetail.getFormComponentValues();
                        for (GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultFormComponentValues formComponentValue : formComponentValues) {
                            String id = formComponentValue.getId();
                            if ("DDDateField-JYNRW51O".equals(id)) {
                                String extValue = formComponentValue.getExtValue();
                                JSONObject jsonObject = JSONObject.parseObject(extValue);
                                String userCheckTime = jsonObject.getString("userCheckTime");
                                dtMendcardOrder.setUserCheckTime(userCheckTime);
                            }
                        }
                        dtMendcardOrder.setInstanceId(instanceId);
                        dtMendcardOrder.setResult("agree");
                        dtMendcardOrder.setEmployeeId(workOrderDetail.getOriginatorUserId());
                        dtMendcardOrder.setName(employeeIdAndName.get(workOrderDetail.getOriginatorUserId()));
                        dtMendcardOrder.setHandleStatus(HandleStatusEnum.未处理_0.getCode());//未处理
                        dtMendcardOrder.setStatus(WorkOrderStatusEnum.COMPLETED.getCode());
                        dtMendcardOrder.setDelFlag(YesOrNo.否_0.getCode());
                        dtMendcardOrder.setCreateBy("1");
                        dtMendcardOrder.setCreateTime(DateUtils.getYYYYMMDDHHSSMM());
                        dtMendcardOrder.setUpdateBy("1");
                        dtMendcardOrder.setUpdateTime(DateUtils.getYYYYMMDDHHSSMM());
                        saves.add(dtMendcardOrder);
                    }
                }
            }
        }

        // 2.补卡工单入库
        if (CollectionUtils.isNotEmpty(saves)) {
            //批量入库
            iDtMendcardOrderService.batchInsert(saves);
        }
        //3.补卡数据入库
        DtMendcardOrder dtMendcardOrder = new DtMendcardOrder();
        dtMendcardOrder.setDelFlag(YesOrNo.否_0.getCode());
        dtMendcardOrder.setHandleStatus(HandleStatusEnum.未处理_0.getCode());
        List<DtMendcardOrder> dtMendcardOrders = iDtMendcardOrderService.selectDtMendcardOrderList(dtMendcardOrder);
/*        List<String> mendCardDate = dtMendcardOrders.stream().map(DtMendcardOrder::getUserCheckTime).collect(Collectors.toList());
        Collections.sort(mendCardDate);*/
        List<DtAttendance> dtAttendances = new ArrayList<>();
        List<String> handleInstanceIds  = new ArrayList<>();
        for (DtMendcardOrder item : dtMendcardOrders) {
            handleInstanceIds.add(item.getInstanceId());
            DtAttendance dtAttendance = new DtAttendance();
            dtAttendance.setType(AttendanceType.补卡记录_2.getCode()); //代表钉钉打卡记录;
            dtAttendance.setEmployeeId(item.getEmployeeId());
            dtAttendance.setName(item.getName());
            String timeStamp = item.getUserCheckTime();
            dtAttendance.setBatchNo(DateUtils.getBatchNo(Long.valueOf(timeStamp)));
            dtAttendance.setUserCheckTimeStamp(timeStamp);
            dtAttendance.setCompanyType(CompanyType.快仓智能科技有限公司_1.getCode());
            dtAttendance.setDataType(AttendanceDataType.钉钉同步_0.getCode());
            dtAttendance.setPlace("补卡");//打卡地区
            dtAttendance.setDelFlag(YesOrNo.否_0.getCode());
            //时间戳转换成时间
            Date userCheckTime = new Date(Long.valueOf(timeStamp));
            try {
                userCheckTime = DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", userCheckTime), "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
                throw new RuntimeException("日期解析出错");
            }
            dtAttendance.setUserCheckTime(userCheckTime);
            String dataId = dtAttendance.getEmployeeId() + dtAttendance.getUserCheckTimeStamp() + dtAttendance.getType();
            dtAttendance.setDataId(dataId);
            dtAttendance.setCreateBy("1");//管理员
            dtAttendance.setCreateTime(DateUtils.getYYYYMMDDHHSSMM());
            dtAttendance.setUpdateBy("1");//管理员
            dtAttendance.setUpdateTime(DateUtils.getYYYYMMDDHHSSMM());
            dtAttendances.add(dtAttendance);
        }
        if(CollectionUtils.isNotEmpty(dtAttendances)){
            //批量入库
            iDtAttendanceService.batchInsertDtAttendance(dtAttendances);

            //4.重新汇总补卡数据
            List<String> userCheckTimeStamps = dtAttendances.stream().map(DtAttendance::getUserCheckTimeStamp).collect(Collectors.toList());
            Collections.sort(userCheckTimeStamps);
            String beginDate = DateUtils.timeConvert(Long.valueOf(userCheckTimeStamps.get(0)));
            String endDate = DateUtils.timeConvert(Long.valueOf(Long.valueOf(userCheckTimeStamps.get(userCheckTimeStamps.size() - 1))));
            iSynAttendanceAnalyzeService.synAttendanceAnalyze(beginDate,endDate);

            //5.批量修改工单状态为已处理
            DtMendcardOrder update = new DtMendcardOrder();
            update.setHandleStatus(HandleStatusEnum.已处理_1.getCode());
            update.setInstanceIds(handleInstanceIds);
            iDtMendcardOrderService.updateDtMendcardOrderByInstanceIds(update);
        }
    }
}
