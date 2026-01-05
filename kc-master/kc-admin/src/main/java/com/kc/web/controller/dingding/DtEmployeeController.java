package com.kc.web.controller.dingding;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.dto.EmployeePageReqeust;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.system.service.ISysDeptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kc.common.annotation.Log;
import com.kc.common.core.controller.BaseController;
import com.kc.common.core.domain.AjaxResult;
import com.kc.common.enums.BusinessType;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.dingtalk.service.IDtEmployeeService;
import com.kc.common.utils.poi.ExcelUtil;
import com.kc.common.core.page.TableDataInfo;

/**
 * 钉钉员工基础信息Controller
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
@RestController
@RequestMapping("/dingtalk/employee")
public class DtEmployeeController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(DtEmployeeController.class);

    @Autowired
    private IDtEmployeeService dtEmployeeService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询钉钉员工基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employee:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmployeePageReqeust reqeust)
    {
        logger.info("查询钉钉员工基础信息列表:当前用户【"+getUsername()+"】"+ JSON.toJSONString(reqeust));
        startPage();
        EmployeeDao pageRequest = new EmployeeDao();
        BeanUtils.copyProperties(reqeust,pageRequest);
        List<DtEmployee> list = dtEmployeeService.selectDtEmployeeList(pageRequest);
        return getDataTable(list);
    }

    /**
     * 导出钉钉员工基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employee:export')")
    @Log(title = "钉钉员工基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmployeePageReqeust pageReqeust)
    {
        EmployeeDao request = new EmployeeDao();
        BeanUtils.copyProperties(pageReqeust,request);
        List<DtEmployee> list = dtEmployeeService.selectDtEmployeeList(request);
        ExcelUtil<DtEmployee> util = new ExcelUtil<DtEmployee>(DtEmployee.class);
        util.exportExcel(response, list, "钉钉员工基础信息数据");
    }

    /**
     * 获取钉钉员工基础信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employee:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dtEmployeeService.selectDtEmployeeById(id));
    }

    /**
     * 新增钉钉员工基础信息
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employee:add')")
    @Log(title = "钉钉员工基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DtEmployee dtEmployee)
    {
        return toAjax(dtEmployeeService.insertDtEmployee(dtEmployee));
    }

    /**
     * 修改钉钉员工基础信息
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employee:edit')")
    @Log(title = "钉钉员工基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DtEmployee dtEmployee)
    {
        return toAjax(dtEmployeeService.updateDtEmployee(dtEmployee));
    }

    /**
     * 删除钉钉员工基础信息
     */
    @PreAuthorize("@ss.hasPermi('dingtalk:employee:remove')")
    @Log(title = "钉钉员工基础信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dtEmployeeService.deleteDtEmployeeByIds(ids));
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('ddingtalk:employee:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }

    /**
     * 获取部门树列表
     */
    @GetMapping("/aaa")
    public AjaxResult aaa(SysDept dept)
    {
        return success("ok");
    }

    /**
     * 查询本月新入职人数
     */
    @GetMapping("/thisMonthNewHireCount")
    public AjaxResult getThisMonthNewHireCount()
    {
        int count = dtEmployeeService.getThisMonthNewHireCount();
        return success(count);
    }
}
