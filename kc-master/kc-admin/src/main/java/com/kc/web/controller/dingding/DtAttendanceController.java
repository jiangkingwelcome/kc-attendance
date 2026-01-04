package com.kc.web.controller.dingding;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.kc.common.core.domain.dao.AttendanceDao;
import com.kc.common.core.domain.dto.AttendancePageReqeust;
import com.kc.common.core.domain.entity.DtAttendance;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.enums.AttendanceDataType;
import com.kc.common.utils.poi.ExcelUtil;
import com.kc.system.service.ISysDeptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kc.common.annotation.Log;
import com.kc.common.core.controller.BaseController;
import com.kc.common.core.domain.AjaxResult;
import com.kc.common.enums.BusinessType;
import com.kc.dingtalk.service.IDtAttendanceService;
import com.kc.common.core.page.TableDataInfo;
import java.util.List;

/**
 * 员工考勤记录Controller
 *
 * @author zhaobo.yang
 * @date 2022-11-24
 */
@RestController
@RequestMapping("/dingtalk/attendance")
public class DtAttendanceController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(DtAttendanceController.class);

    @Autowired
    private IDtAttendanceService dtAttendanceService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询员工考勤记录列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendance:list')")
    @GetMapping("/list")
    public TableDataInfo list(AttendancePageReqeust dtAttendance)
    {
        logger.info("查询员工考勤记录列表:当前用户【"+getUsername()+"】"+ JSON.toJSONString(dtAttendance));
        startPage();
        AttendanceDao query = new AttendanceDao();
        BeanUtils.copyProperties(dtAttendance,query);
        List<DtAttendance> list = dtAttendanceService.selectDtAttendanceList(query);
        return getDataTable(list);
    }

    /**
     * 导出员工考勤记录列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendance:export')")
    @Log(title = "员工考勤记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AttendancePageReqeust dtAttendance)
    {
        AttendanceDao query = new AttendanceDao();
        BeanUtils.copyProperties(dtAttendance,query);
        List<DtAttendance> list = dtAttendanceService.selectDtAttendanceList(query);
        ExcelUtil<DtAttendance> util = new ExcelUtil<DtAttendance>(DtAttendance.class);
        util.exportExcel(response, list, "员工考勤记录数据");
    }

    /**
     * 获取员工考勤记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendance:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dtAttendanceService.selectDtAttendanceById(id));
    }

    /**
     * 新增员工考勤记录
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendance:add')")
    @Log(title = "员工考勤记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DtAttendance dtAttendance)
    {
        dtAttendance.setDataType(AttendanceDataType.本地新增_1.getCode());
        return toAjax(dtAttendanceService.insertDtAttendance(dtAttendance));
    }

    /**
     * 修改员工考勤记录
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendance:edit')")
    @Log(title = "员工考勤记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DtAttendance dtAttendance)
    {
        return toAjax(dtAttendanceService.updateDtAttendance(dtAttendance));
    }

    /**
     * 删除员工考勤记录
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendance:remove')")
    @Log(title = "员工考勤记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dtAttendanceService.deleteDtAttendanceByIds(ids));
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:attendance:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }
}
