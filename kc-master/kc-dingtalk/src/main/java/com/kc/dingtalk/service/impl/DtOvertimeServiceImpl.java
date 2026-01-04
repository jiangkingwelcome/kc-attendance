package com.kc.dingtalk.service.impl;

import com.dingtalk.api.response.OapiAttendanceGetcolumnvalResponse;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.DtOvertime;
import com.kc.common.enums.CompanyType;
import com.kc.common.enums.OverTimeType;
import com.kc.common.enums.YesOrNo;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.DingTalkService;
import com.kc.dingtalk.mapper.DtOvertimeMapper;
import com.kc.dingtalk.service.IDtEmployeeService;
import com.kc.dingtalk.service.IDtOvertimeService;
import com.kc.dingtalk.service.ISynAttendanceAnalyzeService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 员工加班记录Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2023-10-19
 */
@Service
public class DtOvertimeServiceImpl implements IDtOvertimeService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DtOvertimeMapper dtOvertimeMapper;

    @Autowired
    private IDtEmployeeService employeeService;


    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private ISynAttendanceAnalyzeService iSynAttendanceAnalyzeService;

    /**
     * 查询员工加班记录
     *
     * @param id 员工加班记录主键
     * @return 员工加班记录
     */
    @Override
    public DtOvertime selectDtOvertimeById(Long id) {
        return dtOvertimeMapper.selectDtOvertimeById(id);
    }

    /**
     * 查询员工加班记录列表
     *
     * @param dtOvertime 员工加班记录
     * @return 员工加班记录
     */
    @Override
    public List<DtOvertime> selectDtOvertimeList(DtOvertime dtOvertime) {
        return dtOvertimeMapper.selectDtOvertimeList(dtOvertime);
    }

    /**
     * 新增员工加班记录
     *
     * @param dtOvertime 员工加班记录
     * @return 结果
     */
    @Override
    public int insertDtOvertime(DtOvertime dtOvertime) {
        return dtOvertimeMapper.insertDtOvertime(dtOvertime);
    }

    /**
     * 修改员工加班记录
     *
     * @param dtOvertime 员工加班记录
     * @return 结果
     */
    @Override
    public int updateDtOvertime(DtOvertime dtOvertime) {
        return dtOvertimeMapper.updateDtOvertime(dtOvertime);
    }

    /**
     * 批量删除员工加班记录
     *
     * @param ids 需要删除的员工加班记录主键
     * @return 结果
     */
    @Override
    public int deleteDtOvertimeByIds(Long[] ids) {
        return dtOvertimeMapper.deleteDtOvertimeByIds(ids);
    }

    /**
     * 删除员工加班记录信息
     *
     * @param id 员工加班记录主键
     * @return 结果
     */
    @Override
    public int deleteDtOvertimeById(Long id) {
        return dtOvertimeMapper.deleteDtOvertimeById(id);
    }

    /**
     * 批量新增
     *
     * @return 结果
     */
    @Override
    public int batchInsert(List<DtOvertime> list) {
        return dtOvertimeMapper.batchInsert(list);
    }

    @Override
    public int deleteByBatchNoRange(String batchNoStart, String batchNoEnd,List<String> employeeIds,Integer companyType) {
        return dtOvertimeMapper.deleteByBatchNoRange(batchNoStart, batchNoEnd,employeeIds,companyType);
    }

    /**
     * 从钉钉同步加班记录
     *
     * @return 结果
     */
    @Override
    public void synGetOverTimeList(Date startDate, Date endDate,Integer companyType) {
        EmployeeDao queryDao = new EmployeeDao();
        queryDao.setCanDimission(YesOrNo.否_0.getCode());
        queryDao.setCompanyType(companyType);
        List<DtEmployee> employees = employeeService.selectDtEmployeeListNoAuth(queryDao);
        Map<String, String> idAndName = employees.stream().collect(Collectors.toMap(DtEmployee::getEmployeeId, DtEmployee::getName, (value1, value2) -> value1));
        List<String> employeeIds = idAndName.keySet().stream().collect(Collectors.toList());
        List<DtOvertime> saves = new ArrayList<>();
        for (String employeeId : employeeIds) {
            List<OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal> columnDayAndVals = dingTalkService.listOverTime(employeeId,companyType, startDate, endDate);
            if (CollectionUtils.isEmpty(columnDayAndVals)){
                continue;
            }
            for (OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal item : columnDayAndVals) {
                BigDecimal duration = (item.getValue() == null) ? BigDecimal.ZERO : new BigDecimal(item.getValue());
                if (duration.compareTo(BigDecimal.ZERO) == 0){
                    continue;
                }
                Date date = item.getDate();
                String value = item.getValue();
                String batchNo = DateUtils.parseDateToStr("yyyyMMdd", date); //batchNo根据工作日得来
                DtOvertime overtime = new DtOvertime();
                overtime.setEmployeeId(employeeId);
                String name = idAndName.get(employeeId);
                overtime.setName(name);
                overtime.setBatchNo(batchNo);
                overtime.setDuration(value);
                overtime.setDelFlag("0");
                if (CompanyType.快仓智能科技有限公司_1.getCode() == companyType) {
                    overtime.setType("0.5".equals(item.getValue())== true?OverTimeType.半天_0.getCode():OverTimeType.全天_1.getCode());
                }else if (CompanyType.宝仓智能科技苏州有限公司_3.getCode() == companyType){
                    overtime.setType(OverTimeType.小时_2.getCode());
                }else {
                    throw new RuntimeException("未知的加班类型!");
                }
                overtime.setCompanyType(companyType);
                overtime.setDataId(overtime.getEmployeeId() + overtime.getBatchNo() + overtime.getType()+overtime.getCompanyType().toString());
                saves.add(overtime);
            }
        }
        //根据开始时间和结束时间,公司类型，人员删除数据
        deleteByBatchNoRange(DateUtils.parseDateToStr("yyyyMMdd", startDate), DateUtils.parseDateToStr("yyyyMMdd", endDate),employeeIds,companyType);
        //批量入库
        logger.info("【钉钉加班数据入库条数】:" + saves.size());
        batchInsert(saves);
        //考勤汇总
        iSynAttendanceAnalyzeService.synAttendanceAnalyze(DateUtils.parseDateToStr("yyyy-MM-dd",startDate),DateUtils.parseDateToStr("yyyy-MM-dd",endDate));
    }
}
