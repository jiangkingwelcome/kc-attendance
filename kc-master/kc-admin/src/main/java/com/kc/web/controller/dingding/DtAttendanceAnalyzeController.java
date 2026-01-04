package com.kc.web.controller.dingding;

import com.alibaba.fastjson2.JSON;
import com.kc.common.annotation.Log;
import com.kc.common.core.controller.BaseController;
import com.kc.common.core.domain.AjaxResult;
import com.kc.common.core.domain.dao.AttendanceAnalyzeDao;
import com.kc.common.core.domain.dto.AttendanceAnalyzePageRequest;
import com.kc.common.core.domain.entity.DtAttendanceAnalyze;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.core.page.TableDataInfo;
import com.kc.common.enums.BusinessType;
import com.kc.common.utils.StringUtils;
import com.kc.common.utils.poi.ExcelUtil;
import com.kc.dingtalk.service.IDtAttendanceAnalyzeService;
import com.kc.dingtalk.service.IDtEmployeeService;
import com.kc.system.service.ISysDeptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工考勤汇总分析Controller
 *
 * @author zhaobo.yang
 * @date 2022-11-08
 */
@RestController
@RequestMapping("/dingtalk/attendanceAnalyze")
public class DtAttendanceAnalyzeController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DtAttendanceAnalyzeController.class);
    @Autowired
    private IDtAttendanceAnalyzeService dtAttendanceAnalyzeService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IDtEmployeeService employeeService;

    /**
     * 查询员工考勤汇总分析列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:list')")
    @GetMapping("/list")
    public TableDataInfo list(AttendanceAnalyzePageRequest dtAttendanceAnalyze) {
        logger.info("查询员工考勤汇总分析列表:当前用户【"+getUsername()+"】"+ JSON.toJSONString(dtAttendanceAnalyze));
        startPage();
        AttendanceAnalyzeDao query = new AttendanceAnalyzeDao();
        BeanUtils.copyProperties(dtAttendanceAnalyze, query);
        String name = dtAttendanceAnalyze.getName();
        String jobNumber = dtAttendanceAnalyze.getJobNumber();
        if(StringUtils.isNotEmpty(name) && name.contains(",")){
            query.setEmployeeNames(StringUtils.str2List(name, ",", true, true));
            query.setName(null);
        }
        if(StringUtils.isNotEmpty(jobNumber) && jobNumber.contains(",")){
            query.setJobNumbers(StringUtils.str2List(jobNumber, ",", true, true));
            query.setJobNumber(null);
        }
        List<DtAttendanceAnalyze> list = dtAttendanceAnalyzeService.selectDtAttendanceAnalyzeList(query);
        return getDataTable(list);
    }

    /**
     * 导出员工考勤汇总分析列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:export')")
    @Log(title = "员工考勤汇总分析", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AttendanceAnalyzePageRequest dtAttendanceAnalyze) {
        AttendanceAnalyzeDao query = new AttendanceAnalyzeDao();
        BeanUtils.copyProperties(dtAttendanceAnalyze, query);
        String name = dtAttendanceAnalyze.getName();
        String jobNumber = dtAttendanceAnalyze.getJobNumber();
        if(StringUtils.isNotEmpty(name) && name.contains(",")){
            query.setEmployeeNames(StringUtils.str2List(name, ",", true, true));
            query.setName(null);
        }
        if(StringUtils.isNotEmpty(jobNumber) && jobNumber.contains(",")){
            query.setJobNumbers(StringUtils.str2List(jobNumber, ",", true, true));
            query.setJobNumber(null);
        }
        List<DtAttendanceAnalyze> dtAttendanceAnalyzes = dtAttendanceAnalyzeService.selectDtAttendanceAnalyzeList(query);
        //导出的根据name排序，batchNo降序
        List<DtAttendanceAnalyze> list = dtAttendanceAnalyzes.stream().sorted(Comparator.comparing(DtAttendanceAnalyze::getName)
                .thenComparing(DtAttendanceAnalyze::getBatchNo, Comparator.reverseOrder())).collect(Collectors.toList());
        for (DtAttendanceAnalyze attendanceAnalyze : list) {
            String leaveRecord = attendanceAnalyze.getLeaveRecord();
            //excel中的换行符是(char)10
            if (StringUtils.isNotBlank(leaveRecord)){
                String replace = leaveRecord.replace("<br/>", String.valueOf((char) 10));
                attendanceAnalyze.setLeaveRecord(replace);
            }

            //excel中的换行符是(char)10
            String travelRecord = attendanceAnalyze.getTravelRecord();
            if (StringUtils.isNotBlank(travelRecord)){
                String replace = travelRecord.replace("<br/>", String.valueOf((char) 10));
                attendanceAnalyze.setTravelRecord(replace);
            }
        }
        ExcelUtil<DtAttendanceAnalyze> util = new ExcelUtil<DtAttendanceAnalyze>(DtAttendanceAnalyze.class);
        util.exportExcel(response, list, "考勤汇总", "考勤汇总");
    }

    /**
     * 获取员工考勤汇总分析详细信息
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(dtAttendanceAnalyzeService.selectDtAttendanceAnalyzeById(id));
    }

    /**
     * 新增员工考勤汇总分析
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:add')")
    @Log(title = "员工考勤汇总分析", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DtAttendanceAnalyze dtAttendanceAnalyze) {
        return toAjax(dtAttendanceAnalyzeService.insertDtAttendanceAnalyze(dtAttendanceAnalyze));
    }

    /**
     * 修改员工考勤汇总分析
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:edit')")
    @Log(title = "员工考勤汇总分析", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DtAttendanceAnalyze dtAttendanceAnalyze) {
        return toAjax(dtAttendanceAnalyzeService.updateDtAttendanceAnalyze(dtAttendanceAnalyze));
    }

    /**
     * 删除员工考勤汇总分析
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:remove')")
    @Log(title = "员工考勤汇总分析", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(dtAttendanceAnalyzeService.deleteDtAttendanceAnalyzeByIds(ids));
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendanceAnalyze:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept) {
        return success(deptService.selectDeptTreeList(dept));
    }
}
