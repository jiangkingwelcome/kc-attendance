package com.kc.dingtalk.dingding;

import com.alibaba.fastjson2.JSONArray;

/**
 * 汇联易Service接口
 *
 * @author kc
 * @date 2022-11-03
 */
public interface HuiLianYiService {
    /**
     * @description: 获取汇联易token
     * @return:
     * @author: zhaobo.yang
     * @date: 2023/7/4 11:09
     */
    String getToken();

    JSONArray getTravelsFromHuiLianYi(String startDate, String endDate);
}
