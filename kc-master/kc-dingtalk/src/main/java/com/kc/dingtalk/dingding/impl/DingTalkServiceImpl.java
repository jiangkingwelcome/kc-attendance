package com.kc.dingtalk.dingding.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsResponse;
import com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsResponseBody;
import com.aliyun.dingtalkworkflow_1_0.Client;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceHeaders;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceRequest;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponse;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsHeaders;
import com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsRequest;
import com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsResponse;
import com.aliyun.dingtalkworkflow_1_0.models.ListProcessInstanceIdsResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiAttendanceGetcolumnvalRequest;
import com.dingtalk.api.request.OapiAttendanceGetleavestatusRequest;
import com.dingtalk.api.request.OapiAttendanceGetupdatedataRequest;
import com.dingtalk.api.request.OapiAttendanceListRecordRequest;
import com.dingtalk.api.request.OapiCheckinRecordRequest;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiSmartworkHrmEmployeeV2ListRequest;
import com.dingtalk.api.request.OapiV2DepartmentGetRequest;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiAttendanceGetcolumnvalResponse;
import com.dingtalk.api.response.OapiAttendanceGetleavestatusResponse;
import com.dingtalk.api.response.OapiAttendanceGetupdatedataResponse;
import com.dingtalk.api.response.OapiAttendanceListRecordResponse;
import com.dingtalk.api.response.OapiCheckinRecordResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeV2ListResponse;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.core.redis.RedisCache;
import com.kc.common.enums.AgentIdEnum;
import com.kc.common.enums.CompanyType;
import com.kc.common.enums.OverTimeType;
import com.kc.common.enums.WorkOrderStatusEnum;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.DingTalkService;
import com.kc.dingtalk.response.DtDeptResponse;
import com.taobao.api.ApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DingTalkServiceImpl implements DingTalkService {
    private static Logger logger = LoggerFactory.getLogger(DingTalkServiceImpl.class);
    @Autowired
    RedisCache redisCache;
    //快仓智能科技有限公司key和secret
    public static String AppKeyIntellect = "dingdjzudqpvrqlucocs";
    public static String AppSecretIntellect = "jsAmLunzCW0kUdt46fSEkS_cM9LEcad_kJpoBPdS2sJCa1jJd3ruKvB_KGrYsuZS";
    //潍坊公司的key和secret
    public static String AppKeyWF = "dingegyjovb0rlsob2re";
    public static String AppSecretWF = "YQKx9J2esTRgUFO9kQDa_q-Yi7m8VCp4kAQk2lUbK2xcTdMtYoY3v41S9kECWfxi";

    //宝仓的key和secret
    public static String AppKeyBC = "dingnsidsfwkrhgn8khx";
    public static String AppSecretBC = "pCUl5JOtRE_9jv6JjXxw8iok6pEPrEMwtHnMbDAZqP2rIIj8HKVUwMqzbNAaVquG";

    public Client createClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new Client(config);
    }

    /**
     * @param companyType 枚举类型
     * @description:
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/14 11:36
     */
    public String getToken(int companyType) {
        String key = String.format("kc_dingtalk_accessToken:%d", companyType);
        String accessToken = null;
        try {
            accessToken = redisCache.getCacheObject(key);
            if (StringUtils.isBlank(accessToken)) {
                DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
                OapiGettokenRequest request = new OapiGettokenRequest();
                if (companyType == CompanyType.快仓智能科技有限公司_1.getCode()) {
                    request.setAppkey(AppKeyIntellect);
                    request.setAppsecret(AppSecretIntellect);
                } else if (companyType == CompanyType.潍坊众匠人信息技术有限公司_2.getCode()) {
                    request.setAppkey(AppKeyWF);
                    request.setAppsecret(AppSecretWF);
                } else if (companyType == CompanyType.宝仓智能科技苏州有限公司_3.getCode()) {
                    request.setAppkey(AppKeyBC);
                    request.setAppsecret(AppSecretBC);
                } else {
                    throw new RuntimeException("companyType不存在");
                }
                request.setHttpMethod("GET");
                OapiGettokenResponse response = client.execute(request);
                accessToken = response.getAccessToken();
                redisCache.setCacheObject(key, accessToken, 118, TimeUnit.MINUTES);
            }
        } catch (ApiException e) {
            logger.info("【获取accessToken出错】ERRORMSG:" + e.getErrMsg());
            throw new RuntimeException();
        }
        return accessToken;
    }


    /**
     * 根据父部门ID和公司类型列出部门
     *
     * @param parentId    父部门的ID，如果为根部门，则应为null或特定的根部门ID
     * @param companyType 公司类型，用于区分不同类型的公司或部门归属
     * @return 返回一个部门列表，这些部门都属于指定的父部门和公司类型
     */
    public List<DtDeptResponse> listDeptByParentId(Long parentId, int companyType) {
        int times = 1;
        //此方法为根据父部门拉去子部门
        OapiV2DepartmentListsubResponse rsp = null;
        List<DtDeptResponse> depts;
        while (times <= 5) {
            try {
                String accessToken = getToken(companyType);
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
                OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
                req.setDeptId(CompanyType.findByCode(Integer.valueOf(parentId.toString())) != null ? 1L : parentId);
                req.setLanguage("zh_CN");
                logger.info("【部门接口:req】" + JSON.toJSONString(req) + "【accessToken】:" + JSON.toJSONString(accessToken));
                rsp = client.execute(req, accessToken);
                logger.info("【部门接口:rsp】" + JSON.toJSONString(rsp));
                if (!rsp.getErrcode().equals(0L)) {
                    throw new RuntimeException("【获取部门信息出错:ERRORMSG】" + rsp.getErrmsg());
                }
                // 把json字符串转为json对象
                depts = JSON.parseArray(JSONObject.parseObject(rsp.getBody()).getString("result"), DtDeptResponse.class);
                logger.info("【获取部门信息Depts:】" + JSON.toJSONString(depts));
                return depts;
            } catch (ApiException e) {
                if (times > 5) {
                    throw new RuntimeException("【钉钉集成】获取部门信息出错【ERRORMSG】:" + e.getMessage());
                }
                logger.info("【钉钉集成】:拉取部门重试第" + times + "次");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                times++;
            }
        }
        return null;
    }


    /**
     * @description: 拉取部门详情
     * @data: deptId 部门id
     * @data: company 公司类型
     * @author: zhaobo.yang
     * @date: 2022/12/6 15:48
     */
    public SysDept getRootDept(int companyType) {
        // 获取服务端接口调用凭证access_token
        String accessToken = getToken(companyType);
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/get");
        OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
        req.setDeptId(1L);
        OapiV2DepartmentGetResponse rsp = null;
        try {
            logger.info("【拉取部门详情:req】" + JSON.toJSONString(req) + "【accessToken】:" + JSON.toJSONString(accessToken));
            rsp = client.execute(req, accessToken);
            logger.info("【拉取部门详情:rsp】" + JSON.toJSONString(rsp));
            if (!rsp.getErrcode().equals(0L)) {
                throw new RuntimeException("【获取部门详情出错:ERRORMSG】" + rsp.getErrmsg());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        OapiV2DepartmentGetResponse.DeptGetResponse deptResponse = rsp.getResult();
        SysDept sysDept = new SysDept();
        sysDept.setDeptName(deptResponse.getName());
        sysDept.setParentId(deptResponse.getParentId() == null ? 0L : deptResponse.getParentId());
        sysDept.setDeptId(Long.valueOf(companyType));
        sysDept.setAncestors(deptResponse.getParentId() == null ? "0" : deptResponse.getParentId().toString());
        //默认为管理员
        Long userId = 1L;
        //设置当前创建人
        sysDept.setCreateBy(String.valueOf(userId));
        //设置当前修改人
        sysDept.setUpdateBy(String.valueOf(userId));
        return sysDept;
    }


    public List<OapiV2UserListResponse.ListUserResponse> getUsersByDeptId(Long deptId, int companyType, Long nextCursor, Long size) {
        if (nextCursor == null) {
            nextCursor = 0L;
        }
        //获取服务端接口调用凭证access_token
        String accessToken = getToken(companyType);
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
        OapiV2UserListRequest req = new OapiV2UserListRequest();
        req.setDeptId(deptId);
        req.setCursor(nextCursor);
        req.setSize(size);
        req.setOrderField("entry_asc"); //按照进入部门的时间升序
        req.setContainAccessLimit(false);
        req.setLanguage("zh_CN");
        OapiV2UserListResponse rsp = null;
        try {
            logger.info("【获取员工信息请求:req】" + JSON.toJSONString(req) + "【accessToken】:" + JSON.toJSONString(accessToken));
            rsp = client.execute(req, accessToken);
            logger.info("【获取员工信息返回:rsp】" + JSON.toJSONString(rsp));
            if (!rsp.getErrcode().equals(0L)) {
                throw new RuntimeException("获取员工详细信息出错！");
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        List<OapiV2UserListResponse.ListUserResponse> list = rsp.getResult().getList();
        nextCursor = rsp.getResult().getNextCursor();
        Boolean hasMore = rsp.getResult().getHasMore();
        if (hasMore) { //如果有更多数据，需要再次调用
            List<OapiV2UserListResponse.ListUserResponse> users = getUsersByDeptId(deptId, companyType, nextCursor, size);
            if (CollectionUtils.isNotEmpty(users)) {
                list.addAll(users);
            }
        }
        return list;
    }

    /**
     * @param userIds (size <=50)
     * @description: 根据userIds拉取用户打卡详情
     * @author: zhaobo.yang
     * @date: 2022/12/9 16:00
     */
    public List<OapiAttendanceListRecordResponse.Recordresult> getAttendances(List<String> userIds, String begin, String end, int companyType) {
        // 获取服务端接口调用凭证access_token
        String accessToken = getToken(companyType);
        OapiAttendanceListRecordResponse response;
        try {
            // 通过调用接口获取考勤打卡结果
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/attendance/listRecord");
            OapiAttendanceListRecordRequest request = new OapiAttendanceListRecordRequest();
            // 查询考勤打卡记录的开始工作日
            request.setCheckDateFrom(begin + " 00:00:00");
            // 查询考勤打卡记录的结束工作日
            request.setCheckDateTo(end + " 23:59:59");
            // 员工在企业内的userid列表，最多不能超过50个。
            request.setUserIds(userIds);
            logger.info("【打卡接口:req】" + JSON.toJSONString(request) + "【accessToken】:" + JSON.toJSONString(accessToken));
            response = client.execute(request, accessToken);
            logger.info("【打卡接口:rsp】" + JSON.toJSONString(response));
            if (!response.getErrcode().equals(0L)) {
                throw new RuntimeException("【获取打卡信息出错:ERRORMSG】" + response.getErrmsg() + "【ERRCODE】:" + response.getErrcode());
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return response.getRecordresult();
    }

    public List<OapiCheckinRecordResponse.Data> getSignInRecordsByDeptId(String deptId, Long offset, Long size, String begin, String end, int companyType) {
        // 获取服务端接口调用凭证access_token
        String accessToken = getToken(companyType);
        Date beginDateYmdHms = null;
        Date endDateYmdHms = null;
        try {
            beginDateYmdHms = DateUtils.parseDate(begin + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            endDateYmdHms = DateUtils.parseDate(end + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            logger.info("时间日期解析出错");
            throw new RuntimeException("时间日期解析出错");
        }
        // 通过调用接口获取签到记录，部门维度
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/checkin/record");
        OapiCheckinRecordRequest req = new OapiCheckinRecordRequest();
        req.setHttpMethod("GET");
        // 查询考勤打卡记录的开始工作日
        req.setDepartmentId(CompanyType.findByCode(Integer.valueOf(deptId)) != null ? "1" : deptId);
        req.setStartTime(beginDateYmdHms.getTime());
        req.setEndTime(endDateYmdHms.getTime());
        req.setOffset(offset);
        req.setSize(size);
        req.setOrder("desc");
        OapiCheckinRecordResponse rsp = null;
        int times = 1;
        while (times <= 5) {
            try {
                logger.info("【签到接口:req】" + JSON.toJSONString(req) + "【accessToken】:" + JSON.toJSONString(accessToken));
                //执行请求
                rsp = client.execute(req, accessToken);
                logger.info("【签到接口:rsp】" + JSON.toJSONString(rsp));
                if (!rsp.getErrcode().equals(0L)) {
                    throw new RuntimeException("【获取签到信息出错:ERRORMSG】" + rsp.getErrmsg() + "【ERRCODE】:" + rsp.getErrcode());
                }
                List<OapiCheckinRecordResponse.Data> result = rsp.getData();
                //说明可能还有数据,需要再调用
                if (result.size() == size) {
                    List<OapiCheckinRecordResponse.Data> moreData = getSignInRecordsByDeptId(deptId, offset + size, size, begin, end, companyType);
                    if (CollectionUtils.isNotEmpty(moreData)) {
                        result.addAll(moreData);
                    }
                }
                return result;
            } catch (ApiException e) {
                if (times > 5) {
                    throw new RuntimeException("【钉钉集成】根据部门获取签到信息出错【ERRORMSG】:" + e.getMessage());
                }
                logger.info("【钉钉集成】:拉取部门下签到信息重试第" + times + "次");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException("【钉钉集成】根据部门获取签到信息出错【ERRORMSG】:" + ex.getMessage());
                }
                times++;
            }
        }
        return null;
    }


    /**
     * 获取钉钉工单实例ids
     *
     * @return List<String> 钉钉工单ids
     * @throws Exception
     */

    public List<String> getWorkOrderInstanceIds(Long nextToken, Long startTimeStamp, Long endTimeStamp, String processCode) {
        logger.info("【钉钉集成】工单数据");
        //获取服务端接口调用凭证access_token
        String accessToken = getToken(CompanyType.快仓智能科技有限公司_1.getCode());
        ListProcessInstanceIdsHeaders headers = new ListProcessInstanceIdsHeaders();
        Client client = null;
        try {
            //获取token
            headers.xAcsDingtalkAccessToken = accessToken;
            client = createClient();
        } catch (Exception e) {
            logger.info("获取client出错!【error】:" + JSON.toJSONString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        List<String> instanceIds = new ArrayList<>();
        try {
            //设置请求参数
            ListProcessInstanceIdsRequest request = new ListProcessInstanceIdsRequest()
                    .setStartTime(startTimeStamp)
                    .setEndTime(endTimeStamp)
                    .setProcessCode(processCode)
                    .setNextToken(nextToken)
                    .setMaxResults(20L)
                    .setStatuses(Arrays.asList(WorkOrderStatusEnum.COMPLETED.getCode()));
            //初始化实例id
            ListProcessInstanceIdsResponse response = client.listProcessInstanceIdsWithOptions(request, headers, new RuntimeOptions());
            logger.info("获取钉钉工单response:" + JSON.toJSONString(response));
            Boolean success = response.getBody().getSuccess();
            if (!success) {
                logger.info("获取钉钉工单出错！");
                return null;
            }
            ListProcessInstanceIdsResponseBody.ListProcessInstanceIdsResponseBodyResult result = response.getBody().getResult();
            String nt = result.getNextToken();
            instanceIds = result.getList();
            System.out.println("工单实例ids:" + JSON.toJSONString(instanceIds));
            List<String> nextInstanceIds = new ArrayList<>();
            if (StringUtils.isNotBlank(nt)) {
                nextInstanceIds = getWorkOrderInstanceIds(Long.valueOf(nt), startTimeStamp, endTimeStamp, processCode);
            }
            if (CollectionUtils.isNotEmpty(nextInstanceIds)) {
                instanceIds.addAll(nextInstanceIds);
            }
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                logger.info("【钉钉集成】获取工单实例ids出错【TeaException】:" + err.getMessage() + "【CODE】:" + err.getCode() + "ErrorDetail:" + err.getStackTrace());
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                logger.info("【钉钉集成】获取工单实例ids出错【Exception】:" + err.getMessage() + "【CODE】:" + err.getCode() + "ErrorDetail:" + _err.getStackTrace());
            }
        }
        return instanceIds;
    }

    /**
     * 获取钉钉工单实例详情
     *
     * @return Client
     * @throws Exception
     */
    public GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult getWorkOrderDetail(String instanceId) {
        GetProcessInstanceHeaders getProcessInstanceHeaders = new GetProcessInstanceHeaders();
        Client client = null;
        try {
            getProcessInstanceHeaders.xAcsDingtalkAccessToken = getToken(CompanyType.快仓智能科技有限公司_1.getCode());
            client = createClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        GetProcessInstanceRequest getProcessInstanceRequest = new GetProcessInstanceRequest().setProcessInstanceId(instanceId);

        GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult result = null;
        try {
            GetProcessInstanceResponse response = client.getProcessInstanceWithOptions(getProcessInstanceRequest, getProcessInstanceHeaders, new RuntimeOptions());
            return response.getBody().getResult();
        } catch (TeaException err) {
            if (!Common.empty(err.code) && !Common.empty(err.message)) {
                logger.info("【钉钉集成】获取工单实例详情出错【err】:" + err.getMessage() + "【CODE】:" + err.getCode() + "【ErrorDetail】:" + err.getStackTrace());
                throw new RuntimeException(err);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!Common.empty(err.code) && !Common.empty(err.message)) {
                logger.info("【钉钉集成】获取工单实例详情出错【_err】:" + err.getMessage() + "【CODE】:" + err.getCode() + "【ErrorDetail】:" + _err.getStackTrace());
            }
        }
        return null;
    }

    /**
     * 查询请假状态
     */
    public OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo getUpdatedata(String userId, Date workDate, Integer companyType) {
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("userId不可为空！");
        }
        if (workDate == null) {
            throw new RuntimeException("workDate不可为空！");
        }
        if (companyType == null) {
            throw new RuntimeException("companyType不可为空！");
        }
        //获取服务端接口调用凭证access_token
        String accessToken = getToken(companyType);
        int times = 1;
        while (times <= 5) {
            try {
                OapiAttendanceGetupdatedataResponse rsp = null;
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/attendance/getupdatedata");
                OapiAttendanceGetupdatedataRequest req = new OapiAttendanceGetupdatedataRequest();
                req.setUserid(userId);
                req.setWorkDate(workDate);
                logger.info("【拉取请假入参:req】" + JSON.toJSONString(req) + "【accessToken】:" + JSON.toJSONString(accessToken));
                rsp = client.execute(req, accessToken);
                logger.info("【拉取请假出参:rsp】" + JSON.toJSONString(rsp));
                if (!rsp.getErrcode().equals(0L)) {
                    throw new RuntimeException("【拉取请假状态:ERRORMSG】" + rsp.getErrmsg() + "【ERRCODE】:" + rsp.getErrcode());
                }
                OapiAttendanceGetupdatedataResponse.AtCheckInfoForOpenVo result = rsp.getResult();
                return result;
            } catch (ApiException e) {
                if (times > 5) {
                    throw new RuntimeException("【钉钉集成】拉取请假数据出错【ERRORMSG】:" + e.getMessage());
                }
                logger.info("【钉钉集成】:拉取请假数据重试第" + times + "次");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                times++;
            }
        }
        return null;
    }

    /**
     * 查询加班记录
     */
    @Override
    public List<OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal> listOverTime(String userId, Integer companyType, Date startTime, Date endTime) {
        //智能的加班列id
        String znOverTimeColId = "191519953";
        //宝仓的加班列id
        String bcOverTimeColId = "142150820";
        String overtimeColId = null;
        if (CompanyType.快仓智能科技有限公司_1.getCode() == companyType) {
            overtimeColId = znOverTimeColId;
        } else if (CompanyType.宝仓智能科技苏州有限公司_3.getCode() == companyType) {
            overtimeColId = bcOverTimeColId;
        } else {
            return null;
        }
        //获取服务端接口调用凭证access_token
        String accessToken = getToken(companyType);
        int times = 1;
        while (times <= 5) {
            try {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/attendance/getcolumnval");
                OapiAttendanceGetcolumnvalRequest req = new OapiAttendanceGetcolumnvalRequest();
                req.setUserid(userId);
                req.setColumnIdList(overtimeColId); //报表中加班的id
                req.setFromDate(startTime);
                req.setToDate(endTime);
                logger.info("【拉取加班数据:req】" + JSON.toJSONString(req) + "【accessToken】:" + JSON.toJSONString(accessToken));
                OapiAttendanceGetcolumnvalResponse rsp = client.execute(req, accessToken);
                logger.info("【拉取加班数据:rsp】" + JSON.toJSONString(rsp));
                if (!rsp.getErrcode().equals(0L)) {
                    throw new RuntimeException("【拉取加班数据出错:ERRORMSG】" + rsp.getErrmsg() + "【ERRCODE】:" + rsp.getErrcode());
                }
                OapiAttendanceGetcolumnvalResponse.ColumnValListForTopVo result = rsp.getResult();
                List<OapiAttendanceGetcolumnvalResponse.ColumnValForTopVo> columnVals = result.getColumnVals();
                for (OapiAttendanceGetcolumnvalResponse.ColumnValForTopVo columnVal : columnVals) {
                    if (overtimeColId.equals(columnVal.getColumnVo().getId().toString())) {
                        return columnVal.getColumnVals();
                    }
                }
            } catch (ApiException e) {
                if (times > 5) {
                    throw new RuntimeException("【钉钉集成】拉取加班数据出错【ERRORMSG】:" + e.getMessage());
                }
                logger.info("【钉钉集成】:拉取加班数据重试第" + times + "次");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                times++;
            }
        }
        return null;
    }

    /**
     * 获取员工花名册
     */
    @Override
    public List<OapiSmartworkHrmEmployeeV2ListResponse.EmpRosterFieldVo> getEmployeeRosterList(List<String> userId, Integer companyType, String fieldFilterList) {

        if (CollectionUtils.isEmpty(userId)) {
            throw new RuntimeException("员工ids不可为空!");
        }

        if (companyType == null) {
            throw new RuntimeException("公司类型不可为空!");
        }
        String accessToken = getToken(companyType);
        int times = 1;
        while (times <= 5) {
            try {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/smartwork/hrm/employee/v2/list");
                OapiSmartworkHrmEmployeeV2ListRequest req = new OapiSmartworkHrmEmployeeV2ListRequest();
                req.setUseridList(String.join(",", userId));
                req.setFieldFilterList(fieldFilterList);
                req.setAgentid(AgentIdEnum.findByCode(companyType).getInfo());
                logger.info("【拉取员工花名册:req】" + JSON.toJSONString(req));
                OapiSmartworkHrmEmployeeV2ListResponse rsp = client.execute(req, accessToken);
                logger.info("【拉取员工花名册:response】" + JSON.toJSONString(rsp.getResult()));
                return rsp.getResult();
            } catch (ApiException e) {
                if (times > 5) {
                    throw new RuntimeException("【钉钉集成】拉取员工主部门【ERRORMSG】:" + e.getMessage());
                }
                logger.info("【钉钉集成】:拉取员工主部门重试第" + times + "次");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                times++;
            }
        }
        return null;
    }

    @Override
    public  List<ListEmpLeaveRecordsResponseBody.ListEmpLeaveRecordsResponseBodyRecords> getEmployeeDismissionInfo(String startDate, String endDate, String nextToken,Integer companyType) {
        Integer maxResults = 50;
        if (startDate == null) {
            throw new RuntimeException("开始时间不可为空!");
        }

        if (endDate == null) {
            throw new RuntimeException("结束时间不可为空!");
        }

        if (companyType == null) {
            throw new RuntimeException("公司类型不可为空!");
        }

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        com.aliyun.dingtalkcontact_1_0.Client client = null;
        try {
            client = new com.aliyun.dingtalkcontact_1_0.Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<ListEmpLeaveRecordsResponseBody.ListEmpLeaveRecordsResponseBodyRecords> responseList = new ArrayList<>();
        int times = 1;
        while (times <= 5) {
            try {
                com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsHeaders listEmpLeaveRecordsHeaders = new com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsHeaders();
                listEmpLeaveRecordsHeaders.xAcsDingtalkAccessToken = getToken(companyType);
                com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsRequest listEmpLeaveRecordsRequest = new com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsRequest();
                listEmpLeaveRecordsRequest.setStartTime(startDate);
                listEmpLeaveRecordsRequest.setEndTime(endDate);
                listEmpLeaveRecordsRequest.setNextToken(nextToken);
                listEmpLeaveRecordsRequest.setMaxResults(maxResults);
                logger.info("【拉取员工离职信息:req】" + JSON.toJSONString(listEmpLeaveRecordsRequest));
                ListEmpLeaveRecordsResponse listEmpLeaveRecordsResponse = client.listEmpLeaveRecordsWithOptions(listEmpLeaveRecordsRequest, listEmpLeaveRecordsHeaders, new RuntimeOptions());
                logger.info("【拉取员工离职信息:response】" + JSON.toJSONString(listEmpLeaveRecordsResponse));
                if (listEmpLeaveRecordsResponse != null){
                    ListEmpLeaveRecordsResponseBody body = listEmpLeaveRecordsResponse.getBody();
                    if (body != null){
                        nextToken = body.getNextToken();
                        List<ListEmpLeaveRecordsResponseBody.ListEmpLeaveRecordsResponseBodyRecords> records = body.getRecords();
                        if (StringUtils.isNotEmpty(nextToken)){
                            //还需要接着调用
                            List<ListEmpLeaveRecordsResponseBody.ListEmpLeaveRecordsResponseBodyRecords> moreData = getEmployeeDismissionInfo(startDate, endDate, nextToken, companyType);
                            records.addAll( moreData);
                        }
                        return records;
                    }
                }
                return responseList;
            } catch (Exception e) {
                if (times > 5) {
                    throw new RuntimeException("【钉钉集成】拉取员工离职信息出错【ERRORMSG】:" + e.getMessage());
                }
                logger.info("【钉钉集成】:拉取员工离职信息重试第" + times + "次");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                times++;
            }
        }
        return null;
    }
}
