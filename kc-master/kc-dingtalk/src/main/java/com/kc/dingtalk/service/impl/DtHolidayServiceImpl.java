package com.kc.dingtalk.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kc.common.core.domain.entity.DtHoliday;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kc.dingtalk.mapper.DtHolidayMapper;
import com.kc.dingtalk.service.IDtHolidayService;

/**
 * 节假日Service业务层处理
 *
 * @author kc
 * @date 2024-04-30
 */
@Service
public class DtHolidayServiceImpl implements IDtHolidayService {

    private static Logger logger = LoggerFactory.getLogger(DtHolidayServiceImpl.class);
    @Autowired
    private DtHolidayMapper dtHolidayMapper;

    /**
     * 查询节假日
     *
     * @param id 节假日主键
     * @return 节假日
     */
    @Override
    public DtHoliday selectDtHolidayById(Long id) {
        return dtHolidayMapper.selectDtHolidayById(id);
    }

    /**
     * 查询节假日列表
     *
     * @param dtHoliday 节假日
     * @return 节假日
     */
    @Override
    public List<DtHoliday> selectDtHolidayList(DtHoliday dtHoliday) {
        return dtHolidayMapper.selectDtHolidayList(dtHoliday);
    }

    /**
     * 新增节假日
     *
     * @param dtHoliday 节假日
     * @return 结果
     */
    @Override
    public int insertDtHoliday(DtHoliday dtHoliday) {
        dtHoliday.setCreateTime(DateUtils.getNowDate());
        return dtHolidayMapper.insertDtHoliday(dtHoliday);
    }

    /**
     * 修改节假日
     *
     * @param dtHoliday 节假日
     * @return 结果
     */
    @Override
    public int updateDtHoliday(DtHoliday dtHoliday) {
        dtHoliday.setUpdateTime(DateUtils.getNowDate());
        return dtHolidayMapper.updateDtHoliday(dtHoliday);
    }

    /**
     * 批量删除节假日
     *
     * @param ids 需要删除的节假日主键
     * @return 结果
     */
    @Override
    public int deleteDtHolidayByIds(Long[] ids) {
        return dtHolidayMapper.deleteDtHolidayByIds(ids);
    }

    /**
     * 删除节假日信息
     *
     * @param id 节假日主键
     * @return 结果
     */
    @Override
    public int deleteDtHolidayById(Long id) {
        return dtHolidayMapper.deleteDtHolidayById(id);
    }

    /**
     * 执行批量插入操作，将假期信息列表插入到数据库中。
     *
     * @param list 包含待插入假期信息的列表，类型为DtHoliday。
     * @return 返回插入操作影响的行数。
     */
    @Override
    public int batchInsert(List<DtHoliday> list)
    {
        // 调用dtHolidayMapper的batchInsert方法，将列表中的所有假期信息一次性插入数据库
        return dtHolidayMapper.batchInsert(list);
    }

    @Override
    public DtHoliday queryHoliday(String day) {
        if (StringUtils.isBlank(day)){
            logger.info("日期不可为空！");
            return  null;
        }
        String host = "https://jrxxcxsmkj.market.alicloudapi.com";
        String path = "/holiday/search";
        String method = "GET";
        String appcode = "d0709aab0a4a48a08828e7cf30c38e13";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("day", day);
        try {
            logger.info("【查询节假日】入参:{}",day);
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //获取response的body
            JSONObject body = JSON.parseObject(EntityUtils.toString(response.getEntity()));
            logger.info("【查询节假日】出参:{}",JSON.toJSONString(body));
            if (body.getBoolean("success")){
                return JSON.parseObject(body.getString("data"),DtHoliday.class);
            }else{
               throw new RuntimeException ("获取节假日信息失败！MSG:"+body.getString("msg"));
            }
        } catch (Exception e) {
            throw new RuntimeException("获取节假日信息出错!MSG:"+e.getMessage());
        }
    }
}
