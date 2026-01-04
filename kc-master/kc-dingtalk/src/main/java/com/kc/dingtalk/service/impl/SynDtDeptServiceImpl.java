package com.kc.dingtalk.service.impl;

import com.alibaba.fastjson2.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2DepartmentGetRequest;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.kc.common.core.domain.entity.SysDept;
import com.kc.common.enums.CompanyType;
import com.kc.dingtalk.dingding.DingTalkService;
import com.kc.dingtalk.dingding.impl.DingTalkServiceImpl;
import com.kc.dingtalk.response.DtDeptResponse;
import com.kc.dingtalk.service.ISynDtDeptService;
import com.kc.system.service.ISysDeptService;
import com.taobao.api.ApiException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 同步钉钉部门信息
 *
 * @author kc
 * @date 2022-11-03
 */
@Service
@Log4j
public class SynDtDeptServiceImpl implements ISynDtDeptService {

    private static Logger logger = LoggerFactory.getLogger(SynDtDeptServiceImpl.class);

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private DingTalkService dingTalkService;


    /**
     * @description:  同步钉钉部门到数据库
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/4 11:25
     */
    @Override
    public void synDtDept() {
        //同智能、潍坊、宝仓
        List<DtDeptResponse> depts = new ArrayList<>();
        List<SysDept> sysDepts = new ArrayList<>();
        CompanyType[] companyTypes = new CompanyType[]{
               CompanyType.快仓智能科技有限公司_1,
                CompanyType.潍坊众匠人信息技术有限公司_2,
                CompanyType.宝仓智能科技苏州有限公司_3};
        for (CompanyType companyType : companyTypes) {
            SysDept rootDept = dingTalkService.getRootDept(companyType.getCode());
            rootDept.setCompanyType(companyType.getCode());
            //根据parentId获取下级部门
            List<DtDeptResponse> deptByParentId = getDeptByParentId(rootDept.getDeptId(),rootDept.getAncestors(),companyType.getCode());
            if(CollectionUtils.isNotEmpty(deptByParentId)){
                depts.addAll(deptByParentId);
            }
            sysDepts.add(rootDept);
        }

        if (CollectionUtils.isNotEmpty(depts)){
            //删除已存在的部门
            sysDeptService.deleteAll();
        }
        //制造部id
         Long manufacturingDeptId = null;
        //外包id
         Long outsourceDeptId = null;
//        for (DtDeptResponse dept : depts) {
//            String deptName = dept.getDeptName();
//            if("制造部".equals(deptName) && CompanyType.宝仓智能科技苏州有限公司_3.getCode() == dept.getCompanyType()){
//                manufacturingDeptId = dept.getDeptId();
//            }else if("外包".equals(deptName) && CompanyType.宝仓智能科技苏州有限公司_3.getCode() == dept.getCompanyType()){
//                outsourceDeptId = dept.getDeptId();
//            }
//        }

        for (DtDeptResponse dtDeptResponse : depts){
            SysDept sysDept = new SysDept();
            sysDept.setDeptName(dtDeptResponse.getDeptName());
            sysDept.setParentId(dtDeptResponse.getParentId());
            sysDept.setDeptId(dtDeptResponse.getDeptId());
            sysDept.setAncestors(dtDeptResponse.getAncestors());
            sysDept.setCompanyType(dtDeptResponse.getCompanyType());
            //如果是宝仓，只要制造部和外包
            if (CompanyType.宝仓智能科技苏州有限公司_3.getCode() == sysDept.getCompanyType()){
                String ancestors = sysDept.getAncestors();
                List<String> ancestorsList = Arrays.asList(ancestors.split(","));
//                if (!sysDept.getDeptId().equals(manufacturingDeptId) &&
//                    !sysDept.getDeptId().equals(outsourceDeptId)  &&
//                    !ancestorsList.contains(manufacturingDeptId.toString()) &&
//                    !ancestorsList.contains(outsourceDeptId.toString())
//                ){
//                    continue;
//                }
            }
             //默认为管理员
             Long userId = 1L;
            //设置当前创建人
             sysDept.setCreateBy(String.valueOf(userId));
             //设置当前修改人
             sysDept.setUpdateBy(String.valueOf(userId));
             sysDepts.add(sysDept);
        }
        //如果拉取的部门为空则不入库
        if (CollectionUtils.isNotEmpty(sysDepts)) {
            //批量新增
            sysDeptService.batchInsertSysDept(sysDepts);
        }
    }

    /**
     * @description:  通过部门的父id拿到该部门下的所有子部门
     * @param parentId 父部门id
     * @return:
     * @author: zhaobo.yang
     * @date: 2022/11/26 21:25
     */
    public List<DtDeptResponse>  getDeptByParentId(Long parentId,String ancestors,int companyType){
        List<DtDeptResponse> depts = dingTalkService.listDeptByParentId(parentId,companyType);
        List<DtDeptResponse> deptResponses = new ArrayList<>();
        ancestors+=","+parentId;
        if (CollectionUtils.isNotEmpty(depts)){
            for (DtDeptResponse dept : depts) {
                dept.setAncestors(ancestors);
                dept.setParentId(parentId);
                dept.setCompanyType(companyType);
                deptResponses.add(dept);
                List<DtDeptResponse> deptByParentId = getDeptByParentId(dept.getDeptId(), ancestors,companyType);
                if (CollectionUtils.isNotEmpty(deptByParentId)){
                    deptResponses.addAll(deptByParentId);
                }
            }
        }
        return deptResponses;
    }


}
