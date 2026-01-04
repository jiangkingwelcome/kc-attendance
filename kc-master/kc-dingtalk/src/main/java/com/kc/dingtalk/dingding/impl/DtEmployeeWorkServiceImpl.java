package com.kc.dingtalk.dingding.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.dao.EmployeeWorkDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.DtEmployeeWork;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.IDtEmployeeWorkService;
import com.kc.dingtalk.service.IDtEmployeeService;
import com.kc.system.service.ISysDeptService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kc.dingtalk.mapper.DtEmployeeWorkMapper;

/**
 * 员工工作统计Service业务层处理
 *
 * @author kc
 * @date 2025-08-03
 */
@Service
public class DtEmployeeWorkServiceImpl implements IDtEmployeeWorkService {
    @Autowired
    private DtEmployeeWorkMapper dtEmployeeWorkMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private IDtEmployeeService dtEmployeeService;

    /**
     * 查询员工工作统计
     *
     * @return 员工工作统计
     */
    @Override
    public List<DtEmployeeWork> selectEmployeeWorkStat(EmployeeWorkDao employeeWorkDao) {
        List<DtEmployeeWork> dtEmployeeWorks = dtEmployeeWorkMapper.selectEmployeeWorkStat(employeeWorkDao);
        //查询所有员工
        List<DtEmployee> dtEmployees = dtEmployeeService.selectDtEmployeeListNoAuth(new EmployeeDao());
        //查询 智能公司上海工厂下的员工
        List<DtEmployee> shFactory = dtEmployeeService.findAllEmpByDeptId(115572475l);
        //上海工厂员工手机号
        List<String> shFactoryMobiles = new ArrayList<>();
        for (DtEmployee dtEmployee : shFactory) {
            shFactoryMobiles.add(dtEmployee.getMobile());
        }

        Map<String, String> eIdAndMobile = new HashMap<>();
        Map<String, Long> mobileAndMainDept = new HashMap<>();
        for (DtEmployee dtEmployee : dtEmployees) {
            if (StringUtils.isNotBlank(dtEmployee.getEmployeeId())) {
                String key = dtEmployee.getEmployeeId();
                String value = dtEmployee.getMobile();
                eIdAndMobile.put(key, value);
            }

            if (StringUtils.isNotBlank(dtEmployee.getMobile()) && dtEmployee.getMainDept() != null &&
                    dtEmployee.getDeptId() != null && dtEmployee.getMainDept().equals(dtEmployee.getDeptId())) {
                String key = dtEmployee.getMobile() + "_" + dtEmployee.getCompanyType().toString();
                Long value = dtEmployee.getMainDept();
                mobileAndMainDept.put(key, value);
            }
        }

        List<SysDept> allDept = sysDeptService.getAllDept();
        Map<Long, SysDept> deptMap = allDept.stream().collect(Collectors.toMap(SysDept::getDeptId, Function.identity()));
        for (DtEmployeeWork item : dtEmployeeWorks) {
            //开始拼接部门
            String deptNameChain = "";
            String employeeId = item.getEmployeeId();
            String mobile = eIdAndMobile.get(employeeId);
            Long mainDept = null;
            //根据手机号拿到主部门
            if (shFactoryMobiles.contains(mobile)) {
                mainDept = mobileAndMainDept.get(mobile + "_3");
            } else {
                mainDept = mobileAndMainDept.get(mobile + "_1");
            }
            if (mainDept == null || mainDept == 0L || mainDept == -1L || mainDept == 1L || mainDept == 2L || mainDept == 3L) {
                item.setDeptNameChain(deptNameChain);
            } else {
                SysDept sysDept = deptMap.get(mainDept);
                String ancestors = sysDept.getAncestors();
                List<String> ancestorsList =  new ArrayList<>(Arrays.asList(ancestors.split(",")));
                if (mainDept != null){
                    ancestorsList.add(mainDept.toString());
                }
                // 修改后的代码片段
                int count = 0;
                for (String s : ancestorsList) {
                    if (StringUtils.equals(s, "1") || StringUtils.equals(s, "2") || StringUtils.equals(s, "3") || StringUtils.equals(s, "-1") || StringUtils.equals(s, "0")) {
                        continue;
                    }
                    if (count >= 3) { // 保留三个祖先部门
                        break;
                    }
                    SysDept deptName = deptMap.get(Long.valueOf(s));
                    deptNameChain += deptName.getDeptName() + "->";
                    count++;
                }
                // 移除末尾的 "->"
                if (deptNameChain.endsWith("->")) {
                    deptNameChain = deptNameChain.substring(0, deptNameChain.length() - 2);
                }
                item.setDeptNameChain(deptNameChain);
            }
        }
        return dtEmployeeWorks;
    }
}
