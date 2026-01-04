package com.kc.dingtalk.dingding.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.response.OapiAttendanceGetcolumnvalResponse;
import com.dingtalk.api.response.OapiAttendanceGetleavestatusResponse;
import com.dingtalk.api.response.OapiAttendanceGetupdatedataResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.kc.common.core.domain.entity.DtHoliday;
import com.kc.common.core.redis.RedisCache;
import com.kc.common.enums.CompanyType;
import com.kc.common.enums.WorkOrderStatusEnum;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.ExceptionUtil;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.DingTalkService;
import com.kc.dingtalk.dingding.HuiLianYiService;
import com.kc.dingtalk.response.DtDeptResponse;
import com.kc.dingtalk.utils.HttpUtils;
import com.taobao.api.ApiException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class HuiLianYiServiceImpl implements HuiLianYiService {
    private static Logger logger = LoggerFactory.getLogger(HuiLianYiServiceImpl.class);
    @Autowired
    RedisCache redisCache;
    //【测试】汇联易鉴权参数
/*    public static String APP_ID = "375b6f01-cb02-4b87-85c1-96f1ee1b04ee";
    public static String APP_SECRET = "NDJjMTcyNGUtNGQ0Mi00NGY4LThhNDItYTNkNjZmZWIzYWYw";
    public static String HOST = "https://apistage.huilianyi.com/gateway";*/

    //【生产】汇联易测试环境鉴权参数
    public static String APP_ID = "c0b1a83d-3ea5-4293-8c90-64b2d4cf7816";
    public static String APP_SECRET= "MDNiOWJmNzgtNzNiOC00MzdkLTkxMDUtOGVlZTJhOTE2ZWVi";
    public static String HOST= "https://api.huilianyi.com/gateway";

    //鉴权地址
    public static String AUTH_URL = "/oauth/token";
    //获取出差地址
    public static String TRAVEL_APPLICATION_URL = "/api/open/travelApplication";

    /**
     * @description:
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/14 11:36
     */
    public String getToken() {
        String key = "kc_dingtalk_huilianyi_accessToken";
        String accessToken = null;
        try {
            accessToken = redisCache.getCacheObject(key);
            if (StringUtils.isBlank(accessToken)) {
                String host = HOST;
                String path = AUTH_URL;
                String method = "POST";
                //设置请求参数
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("grant_type", "client_credentials");
                queryParams.put("scope", "write");
                Map<String, String> headers = new HashMap<String, String>();
                //设置表头
                String authStr = APP_ID.concat(":").concat(APP_SECRET);
                String authStrEnc = new String(Base64.encodeBase64(authStr.getBytes()));
                headers.put("Authorization", "Basic ".concat(authStrEnc));
                HttpResponse response = HttpUtils.doPost(host, path, method, headers, queryParams, (String) null);
                //获取response的body
                logger.info("开始获取汇联易token！");
                JSONObject body = JSON.parseObject(EntityUtils.toString(response.getEntity()));
                logger.info("获取汇联易token！"+body.toString());
                accessToken = body.getString("access_token");
                Assert.isTrue(StringUtils.isNotBlank(accessToken), "accessToken为空!");
                Integer expiresIn = body.getInteger("expires_in");
                redisCache.setCacheObject(key, accessToken, expiresIn, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new RuntimeException("【获取accessToken出错】ERRORMSG:" + e.getMessage());
        }
        return accessToken;
    }

    public JSONArray getTravelsFromHuiLianYi(String startDate, String endDate) {
        JSONArray result = new JSONArray();
        //获取鉴权参数
        String accessToken = getToken();
        //设置表头
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer ".concat(accessToken));
        //请求主机
        String host = HOST;
        //接口地址
        String path = TRAVEL_APPLICATION_URL;
        String method = "GET";
        //分页
        Integer page = 1;
        Integer size = 100;
        //审批通过和作废的
        String status = "1003,1012";
        //设置请求参数
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("startDate", startDate);
        queryParams.put("endDate", endDate);
        queryParams.put("status",status);
        queryParams.put("size",String.valueOf(size));
        queryParams.put("withApplicationParticipant","true");
        Integer curCount = 0;
        do{
            queryParams.put("page",String.valueOf(page));
            HttpResponse response = null;
            try {
                logger.info("拉取汇联易出差、公出数据：{}",JSON.toJSONString(queryParams));
                response = HttpUtils.doGet(host, path, method, headers, queryParams);
                //获取response的body
                JSONArray objects = JSON.parseArray(EntityUtils.toString(response.getEntity()));
                if (objects != null && objects.size() != 0){
                    result.addAll(objects);
                    curCount = objects.size();
                }else{
                    curCount = 0;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            page ++;
        }while (curCount.equals(size));
        return result;
    }
}
