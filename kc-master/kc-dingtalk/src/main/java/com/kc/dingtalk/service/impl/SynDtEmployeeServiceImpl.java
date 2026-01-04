package com.kc.dingtalk.service.impl;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dingtalkcontact_1_0.models.ListEmpLeaveRecordsResponseBody;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeV2ListResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.kc.common.constant.UserConstants;
import com.kc.common.core.domain.dao.EmployeeDao;
import com.kc.common.core.domain.dao.EmployeeSaveDao;
import com.kc.common.core.domain.entity.DtEmployee;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.core.domain.entity.SysUser;
import com.kc.common.enums.CompanyType;
import com.kc.common.enums.YesOrNo;
import com.kc.common.utils.DateUtils;
import com.kc.common.utils.SecurityUtils;
import com.kc.common.utils.StringUtils;
import com.kc.dingtalk.dingding.impl.DingTalkServiceImpl;
import com.kc.dingtalk.mapper.DtEmployeeMapper;
import com.kc.dingtalk.service.ISynDtEmployeeService;
import com.kc.system.mapper.SysDeptMapper;
import com.kc.system.service.ISysUserService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 同步钉钉员工基础信息Service业务层处理
 *
 * @author zhaobo.yang
 * @date 2022-11-06
 */
@Service
public class SynDtEmployeeServiceImpl implements ISynDtEmployeeService {
    private static Logger logger = LoggerFactory.getLogger(SynDtDeptServiceImpl.class);

    @Autowired
    private DtEmployeeMapper dtEmployeeMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private DingTalkServiceImpl dingTalkService;

    @Autowired
    private ISysUserService userService;

    /**
     * @description: 同步钉钉员工基础信息
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/6 13:51
     */
    @Override
    public void synDtEmployee() {
        //访问接口获取所有钉钉员工
        CompanyType[] companyTypes = new CompanyType[]{
               CompanyType.快仓智能科技有限公司_1,
               CompanyType.潍坊众匠人信息技术有限公司_2,
               CompanyType.宝仓智能科技苏州有限公司_3
        };
        Map<String, List<OapiV2UserListResponse.ListUserResponse>> deptEmployees = new HashMap<>();
        List<String> employeeIds = new ArrayList<>();
        for (CompanyType companyType : companyTypes) {
            //获取本地部门
            SysDept query = new SysDept();
            query.setCompanyType(companyType.getCode());
            List<SysDept> sysDepts = sysDeptMapper.selectDeptList(query);
            if (Collections.isEmpty(sysDepts)) {
                break;
            }
            //获取本地部门ids
            List<Long> deptIds = sysDepts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
            if (Collections.isEmpty(deptIds)) {
                break;
            }
            for (Long dId : deptIds) {
                //根据部门id从钉钉拉取部门人员
                List<OapiV2UserListResponse.ListUserResponse> employees = dingTalkService.getUsersByDeptId(
                        CompanyType.findByCode(Integer.valueOf(dId.toString())) != null ? 1L : dId,
                        companyType.getCode(),
                        0L,
                        100L
                );
                if (CollectionUtils.isNotEmpty(employees)) {
                    employeeIds.addAll(employees.stream().map(OapiV2UserListResponse.ListUserResponse::getUserid).collect(Collectors.toList()));
                    deptEmployees.put(dId + "_" + companyType.getCode(), employees);
                }
            }
        }

        Map<Long, String> deptLeader = new HashMap<>();
        //整合员工入库数据
        if (MapUtils.isNotEmpty(deptEmployees)) {
            if (CollectionUtils.isNotEmpty(employeeIds)) {
                dtEmployeeMapper.deleteByEmployeeIds(employeeIds);
                //修改为离职
                dtEmployeeMapper.dimissionAll();
            }
            List<EmployeeSaveDao> saves = new ArrayList<>();
            for (Map.Entry<String, List<OapiV2UserListResponse.ListUserResponse>> entry : deptEmployees.entrySet()) {
                List<OapiV2UserListResponse.ListUserResponse> employees = entry.getValue();
                Long deptId = Long.valueOf(entry.getKey().split("_")[0]);
                Integer companyType = Integer.valueOf(entry.getKey().split("_")[1]);
                for (OapiV2UserListResponse.ListUserResponse employee : employees) {
                    //处理人员数据
                    EmployeeSaveDao saveDao = handleEmployee(deptLeader, deptId, companyType, employee);
                    saves.add(saveDao);
                }
            }
            //数据入库
            if (CollectionUtils.isNotEmpty(saves)) {
                //人员数据入库
                dtEmployeeMapper.batchInsertDtEmployee(saves);
            }
            //修改部门
            for (Map.Entry<Long, String> entry : deptLeader.entrySet()) {
                Long deptId = entry.getKey();
                String leader = entry.getValue();
                SysDept sysDept = new SysDept();
                sysDept.setDeptId(deptId);
                sysDept.setLeader(leader);
                sysDeptMapper.updateDept(sysDept);
            }

            /*for (CompanyType companyType : companyTypes) {
                if (companyType == CompanyType.快仓智能科技有限公司_1){
                    //获取人员主部门
                    // 1. 查询智能下面 Shanghai Factory/上海工厂（宝仓）的所有人（包括离职人员）
                    List<DtEmployee> shFactoryEmployees = dtEmployeeMapper.findAllEmpByDeptId(115899065L);
                    // 2. 查询 智能下面的所有人 排除掉 上海工厂（宝仓）的员工
                    List<DtEmployee> znEmployees = dtEmployeeMapper.findAllEmpByDeptId(1L);

                    //3.智能员工中根据工号排除掉 上海工厂（宝仓）的员工
                    Set<String> shFactoryJobNumbers = shFactoryEmployees.stream()
                            .map(DtEmployee::getJobNumber)
                            //排除掉工号为空的
                            .filter(jobNumber -> StringUtils.isNotBlank(jobNumber))
                            .collect(Collectors.toSet());
                    List<DtEmployee> result = znEmployees.stream()
                            .filter(emp -> !shFactoryJobNumbers.contains(emp.getJobNumber()))
                            .collect(Collectors.toList());
                    //获取智能员工的主部门
                    //result分割成100每组

                }
            }*/
        }
    }

    private  EmployeeSaveDao handleEmployee(Map<Long, String> deptLeader, Long deptId, Integer companyType, OapiV2UserListResponse.ListUserResponse employee) {
        //特殊离职人员
        List<String> candimission = new ArrayList<>(Arrays.asList("122018626121896803", "030330080526068468", "0303300805594202228"));
        //特殊工号人员
        Map<String,String> userIdAndJobNum = new HashMap<>();
        userIdAndJobNum.put("17236934322224128","F01488"); //张康
        userIdAndJobNum.put("025030351054779159","F01488");//张康
        userIdAndJobNum.put("126902581535396355","F01664");//贾传健
        userIdAndJobNum.put("17391523574185467","F01664");//贾传健

        EmployeeSaveDao saveDao = new EmployeeSaveDao();
        String userId = employee.getUserid();
        saveDao.setName(employee.getName());
        saveDao.setEmployeeId(userId);
        saveDao.setDeptId(deptId);
        saveDao.setDataId(saveDao.getEmployeeId() + deptId.toString()+ companyType.toString());
        saveDao.setUnionId(employee.getUnionid());
        saveDao.setMobile(employee.getMobile());
        saveDao.setTel(employee.getTelephone());
        saveDao.setWorkPlace(employee.getWorkPlace());
        saveDao.setRemark(employee.getRemark());
        saveDao.setCompanyType(companyType);
        saveDao.setEmail(employee.getEmail());
        saveDao.setOrgEmail(employee.getOrgEmail());
        saveDao.setAvatar(employee.getAvatar());
        saveDao.setExtattr(employee.getExtension());
        Long hiredDate = employee.getHiredDate();
        if (employee.getLeader()) {
            String leader = deptLeader.get(deptId);
            if (StringUtils.isNotBlank(leader)) {
                leader += "," + employee.getUserid();
            } else {
                leader = employee.getUserid();
            }
            deptLeader.put(deptId, leader);
        }
        if (hiredDate != null) {
            Date date = new Date();
            date.setTime(hiredDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(date);
            Date date1 = DateUtils.parseDate(format);
            saveDao.setHiredDate(date1);
        }
        if (StringUtils.isNotBlank(employee.getUserid()) && userIdAndJobNum.containsKey(employee.getUserid())){
            saveDao.setJobNumber(userIdAndJobNum.get(employee.getUserid()));
        }else{
            if (StringUtils.isBlank(employee.getJobNumber())) {
                saveDao.setJobNumber("");
            } else {
                String jobNumber = employee.getJobNumber();
                saveDao.setJobNumber(jobNumber);
                Integer empType = null;
                if (StringUtils.startsWithIgnoreCase(jobNumber, "i")){
                    empType = 0 ; //实习生
                }else if (StringUtils.startsWithIgnoreCase(jobNumber, "v")){
                    empType = 1 ;//外包
                }
                saveDao.setEmpType(empType);
            }
        }
        saveDao.setCanAdmin(employee.getAdmin() ? "1" : "0");
        saveDao.setCanBoss(employee.getBoss() ? "1" : "0");
        saveDao.setCanHide(employee.getHideMobile() ? "1" : "0");
        saveDao.setDelFlag(YesOrNo.否_0.getCode());
        saveDao.setUpdateFlag(YesOrNo.否_0.getCode());
        if (StringUtils.isNotBlank(employee.getUserid()) && candimission.contains(employee.getUserid())){
            saveDao.setCanDimission("1");
        }else {
            saveDao.setCanDimission(YesOrNo.否_0.getCode());
        }
        Date dateTime = DateUtils.getYYYYMMDDHHSSMM();
        saveDao.setCreateBy("1");//管理员
        saveDao.setCreateTime(dateTime);
        saveDao.setUpdateTime(dateTime);
        saveDao.setUpdateBy("1");//管理员
        return saveDao;
    }


    /**
     * @description: 批量开通人员用户
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/12/15 14:23
     */

    @Override
    public void batchCreateUser() {
        //查询未开通用户的钉钉员工
        List<DtEmployee> dtEmployees = dtEmployeeMapper.getUnCreateAccountEmployees();
        for (DtEmployee dtEmployee : dtEmployees) {
            SysUser user = new SysUser();
            //默认密码123465
            user.setPassword("123456");
            user.setCreateBy("1");//管理员
            user.setUserName(dtEmployee.getJobNumber());
            user.setNickName(dtEmployee.getName());
            user.setSex("2");//性别未知
            user.setRoleIds(new Long[]{102L});
            user.setEmployeeId(dtEmployee.getEmployeeId());
            if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user))) {
                String errorMsg = "新增用户'" + user.getUserName() + "'失败，登录账号已存在";
                throw new RuntimeException(errorMsg);
            } else {
                user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
                userService.insertUser(user);
              /*  Long userId = user.getUserId();
                SysUserEmployee sysUserEmployee = new SysUserEmployee();
                sysUserEmployee.setUserId(userId);
                sysUserEmployee.setEmployeeId(dtEmployee.getEmployeeId());
                userEmployeeService.insertSysUserEmployee(sysUserEmployee);*/
            }
        }
    }

    @Override
    public void synIdCardAndJobNumAndMainDept(Integer companyType) {
        //查询所有员工
        EmployeeDao queryDao = new EmployeeDao();
        queryDao.setCompanyType(companyType);
        List<DtEmployee> employees = dtEmployeeMapper.selectDtEmployeeList(queryDao);
        //根据DtEmployee中的employeeId去重
        List<String> employeeIds = employees.stream().map(DtEmployee::getEmployeeId).distinct().collect(Collectors.toList());
        List<DtEmployee> updates = new ArrayList<>();
        //查询员工花名册
        List<List<String>> partition = ListUtils.partition(employeeIds, 100);
        for (List<String> employeeIdsPartition : partition) {
            List<OapiSmartworkHrmEmployeeV2ListResponse.EmpRosterFieldVo> employeeRosterList = dingTalkService.getEmployeeRosterList(employeeIdsPartition, companyType, "sys00-mainDeptId,sys00-jobNumber,sys02-certNo,sys01-employeeStatus,sys00-confirmJoinTime");
            for (OapiSmartworkHrmEmployeeV2ListResponse.EmpRosterFieldVo empRosterFieldVo : employeeRosterList) {
                List<OapiSmartworkHrmEmployeeV2ListResponse.EmpFieldDataVo> fieldDataList = empRosterFieldVo.getFieldDataList();
                String userid = empRosterFieldVo.getUserid();
                DtEmployee update = new DtEmployee();
                update.setEmployeeId(userid);
                update.setCompanyType(companyType);
                update.setUpdateBy("1");
                update.setUpdateTime(DateUtils.getYYYYMMDDHHSSMM());
                Boolean flag = false;//是否有值
                for (OapiSmartworkHrmEmployeeV2ListResponse.EmpFieldDataVo fieldData : fieldDataList) {
                    String fieldCode = fieldData.getFieldCode();
                    List<OapiSmartworkHrmEmployeeV2ListResponse.FieldValueVo> fieldValueList = fieldData.getFieldValueList();
                    OapiSmartworkHrmEmployeeV2ListResponse.FieldValueVo fieldValueVo = fieldValueList.get(0);
                    String value = fieldValueVo.getValue();

                    if (StringUtils.isNotEmpty(value) ){
                        value = value.trim();
                        if ("sys00-mainDeptId".equals(fieldCode)){
                            update.setMainDept(Long.valueOf(value));
                            flag = true;
                        }
                        if ("sys00-jobNumber".equals(fieldCode)){
                            update.setJobNumber(value);
                            flag = true;
                        }
                        if ("sys02-certNo".equals(fieldCode)){
                            update.setCardNum(value);
                            flag = true;
                        }
                        if("sys01-employeeStatus".equals(fieldCode) && value.equals("4")){
                            update.setCanDimission("1");//已离职
                            flag = true;
                        }
                        if ("sys00-confirmJoinTime".equals(fieldCode)){ //入职日期
                            update.setHiredDate(DateUtils.parseDate(value));
                            flag = true;
                        }
                    }
                }
                if (flag){
                    updates.add(update);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(updates)){
            dtEmployeeMapper.batchUpdate(updates);
        }
    }


    @Override
    public void synEmployeeDismissionInfo(String startDateStr, String endDateStr, Integer companyType) {
        List<ListEmpLeaveRecordsResponseBody.ListEmpLeaveRecordsResponseBodyRecords> responseList = dingTalkService.getEmployeeDismissionInfo(startDateStr, endDateStr, "0", companyType);
        List<DtEmployee> updates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(responseList)){
            for (ListEmpLeaveRecordsResponseBody.ListEmpLeaveRecordsResponseBodyRecords item : responseList) {
                String userId = item.getUserId();
                String leaveTime = item.getLeaveTime();
                Date leaverTimeBeijingTime = DateUtils.convertFromUtcToBeijingTime(leaveTime);
                if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(leaveTime) ){
                    DtEmployee update = new DtEmployee();
                    update.setEmployeeId(userId);
                    update.setCompanyType(companyType);
                    update.setLastWorkDay(leaverTimeBeijingTime);
                    update.setCanDimission("1");
                    update.setUpdateBy("1");
                    update.setUpdateTime(DateUtils.getYYYYMMDDHHSSMM());
                    updates.add(update);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(updates)){
            dtEmployeeMapper.batchUpdate(updates);
        }
    }

    @Override
    public void handleEmpType() {
        //查询所有员工
        EmployeeDao queryDao = new EmployeeDao();
        List<DtEmployee> employees = dtEmployeeMapper.selectDtEmployeeList(queryDao);
        List<DtEmployee> updates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(employees)){
            for (DtEmployee item : employees) {
                DtEmployee update = new DtEmployee();
                String jobNumber = item.getJobNumber();
                if (StringUtils.isNotEmpty(jobNumber)){
                    if (StringUtils.startsWithIgnoreCase(jobNumber,"i")){ //实习生
                        update.setEmpType(0);
                        update.setId(item.getId());
                        updates.add( update);
                    }else if (StringUtils.startsWithIgnoreCase(jobNumber,"v")){ //外包
                        update.setEmpType(1);
                        update.setId(item.getId());
                        updates.add( update);
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(updates)){
            dtEmployeeMapper.batchUpdate(updates);
        }
    }
}
