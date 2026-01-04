package com.kc.web.controller.dingding;

import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.kc.common.annotation.Log;
import com.kc.common.core.domain.AjaxResult;
import com.kc.common.core.domain.dao.EmployeeWorkDao;
import com.kc.common.core.domain.dto.EmployeeWorkPageRequest;
import com.kc.common.core.domain.entity.DtEmployeeWork;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.enums.BusinessType;
import com.kc.common.utils.StringUtils;
import com.kc.common.utils.poi.ExcelUtil;
import com.kc.dingtalk.dingding.IDtEmployeeWorkService;
import com.kc.system.service.ISysDeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kc.common.core.controller.BaseController;
import com.kc.common.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 员工工作统计Controller
 *
 * @author kc
 * @date 2025-08-03
 */
@RestController
@RequestMapping("/dingtalk/employeeWork")
public class DtEmployeeWorkController extends BaseController
{
    @Autowired
    private IDtEmployeeWorkService dtEmployeeWorkService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询员工工作统计列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employeeWork:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmployeeWorkPageRequest pageRequest)
    {
        logger.info("查询员工工作统计列表:当前用户【"+getUsername()+"】"+ JSON.toJSONString(pageRequest));
        String beginBatchNo = (String)pageRequest.getParams().get("beginBatchNo");
        String endBatchNo = (String)pageRequest.getParams().get("endBatchNo");
        if (StringUtils.isBlank(beginBatchNo) || StringUtils.isBlank(endBatchNo)){
            throw new RuntimeException("请选择日期批次!");
        }
        startPage();
        EmployeeWorkDao query = new EmployeeWorkDao();
        BeanUtils.copyProperties(pageRequest, query);
        List<DtEmployeeWork> list = dtEmployeeWorkService.selectEmployeeWorkStat(query);
        return getDataTable(list);
    }

    /**
     * 导出员工工作统计列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employeeWork:export')")
    @Log(title = "员工工作统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmployeeWorkPageRequest pageRequest)
    {
        logger.info("导出员工工作统计列表:当前用户【"+getUsername()+"】"+ JSON.toJSONString(pageRequest));
        String beginBatchNo = (String)pageRequest.getParams().get("beginBatchNo");
        String endBatchNo = (String)pageRequest.getParams().get("endBatchNo");
        if (StringUtils.isBlank(beginBatchNo) || StringUtils.isBlank(endBatchNo)){
            throw new RuntimeException("请选择日期批次!");
        }
        EmployeeWorkDao query = new EmployeeWorkDao();
        BeanUtils.copyProperties(pageRequest, query);
        List<DtEmployeeWork> list = dtEmployeeWorkService.selectEmployeeWorkStat(query);
        ExcelUtil<DtEmployeeWork> util = new ExcelUtil<DtEmployeeWork>(DtEmployeeWork.class);
        util.exportExcel(response, list, "员工考勤记录数据");
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employeeWork:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }


}
