package com.kc.web.controller.tool;

import com.kc.common.core.controller.BaseController;
import com.kc.common.core.domain.dao.AttendanceAnalyzeDao;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.dto.AttendanceAnalyzePageRequest;
import com.kc.common.core.domain.dto.EmployeePageReqeust;
import com.kc.common.core.domain.entity.DtAttendanceAnalyze;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.page.TableDataInfo;
import com.kc.dingtalk.service.IDtAttendanceAnalyzeService;
import com.kc.dingtalk.service.IDtEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * swagger 用户测试方法
 *
 * @author kc
 */
@Api("用户信息管理")
@RestController
@RequestMapping("/openApi/dingTalk")
public class DingTalkOpenApiController extends BaseController
{
    @Autowired
    private IDtAttendanceAnalyzeService dtAttendanceAnalyzeService;

    @Autowired
    private IDtEmployeeService dtEmployeeService;

    /**
     * 查询员工考勤汇总分析列表
     */
    @ApiOperation("获取人员考勤汇总")
    @PostMapping("/attendanceAnalyze/list")
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:list')")
    public TableDataInfo list(@RequestBody AttendanceAnalyzePageRequest dtAttendanceAnalyze) {
        startPage();
        AttendanceAnalyzeDao query = new AttendanceAnalyzeDao();
        BeanUtils.copyProperties(dtAttendanceAnalyze, query);
        List<DtAttendanceAnalyze> list = dtAttendanceAnalyzeService.selectDtAttendanceAnalyzeList(query);
        return getDataTable(list);
    }

    /**
     * 查询员工考勤汇总分析列表
     */
    @ApiOperation("获取人员考勤汇总")
    @PostMapping("/employee/list")
    @PreAuthorize("@ss.hasPermi('dingtalk:employee:list')")
    public TableDataInfo list(@RequestBody EmployeePageReqeust reqeust) {
        startPage();
        EmployeeDao pageRequest = new EmployeeDao();
        BeanUtils.copyProperties(reqeust,pageRequest);
        List<DtEmployee> list = dtEmployeeService.selectDtEmployeeList(pageRequest);
        return getDataTable(list);
    }
}
