<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入员工名称"
          clearable
          @keyup.enter.native="handleQuery"
          style="width:150px"
        />
      </el-form-item>

      <el-form-item label="工号" prop="jobNumber">
        <el-input
          v-model="queryParams.jobNumber"
          placeholder="请输入工号"
          clearable
          @keyup.enter.native="handleQuery"
          style="width:150px"
        />
      </el-form-item>

      <el-form-item label="日期批次" prop="daterangeBatchNo">
        <el-date-picker
          v-model="daterangeBatchNo"
          style="width: 240px"
          value-format="yyyyMMdd"
          format = "yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          aria-required="true"
        ></el-date-picker>
      </el-form-item>

      <el-form-item label="部门" prop="deptId">
        <treeselect style="width: 400px" v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dingtalk:employeeWork:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="employeeWorkList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
      <el-table-column label="员工姓名" align="center" prop="name" />
      <el-table-column label="工号" align="center" prop="jobNumber" />
      <el-table-column label="人员类型" align="center" prop="empType" width="150">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.emp_type" :value="scope.row.empType"/>
        </template>
      </el-table-column>
      <el-table-column label="部门" align="center" prop="deptNameChain" />
      <el-table-column label="考勤总工时(不含加班、请假)" align="center" prop="durationtotal" />
      <el-table-column label="考勤天数(不含加班、请假)" align="center" prop="durationdays" />
      <el-table-column label="考勤平均工时" align="center" prop="avgduration" />
      <el-table-column label="应出勤天数(天)" align="center" prop="ycounts" />
      <el-table-column label="应出未出天数(天)" align="center" prop="ywcounts" />
      <el-table-column label="应出未出日期" align="center" prop="ywdays" />
      <el-table-column label="请假次数" align="center" prop="leavecount" />
      <el-table-column label="请假时长(天)" align="center" prop="leaveduration" />
      <el-table-column label="半天假考勤时长" align="center" prop="leavehalfdayduration" />
      <el-table-column label="加班次数" align="center" prop="overtimecount" />
      <el-table-column label="加班申请时长(天)" align="center" prop="overtimeapplydurationDay" />
      <el-table-column label="加班申请时长(小时)" align="center" prop="overtimeapplydurationHour" />
      <el-table-column label="加班打卡时长(小时)" align="center" prop="overtimeduration" />
      <el-table-column label="总工时(含加班、请假半天)" align="center" prop="allduration" />
      <el-table-column label="实际出勤天数(天)" align="center" prop="scount" />
      <el-table-column label="平均工时(小时)" align="center" prop="savgduration" />
      <el-table-column label="出差次数" align="center" prop="travelcount" />
      <el-table-column label="出差时长(天)" align="center" prop="traveldays" />
      <el-table-column label="出差时长(小时)" align="center" prop="travelduration" />
      <el-table-column label="迟到&工时不足(天数)" align="center" prop="lateworkUndertime" />
      <el-table-column label="迟到&工时满足(天数)" align="center" prop="lateworkAbovetime" />
      <el-table-column label="准时&工时不足(天数)" align="center" prop="earlyworkUndertime" />
      <el-table-column label="异常类型" align="center" prop="anomalyType" width="120">
        <template slot-scope="scope">
          <el-tag
            :type="getAnomalyTagType(scope.row.anomalyType)"
            disable-transitions>
            {{ scope.row.anomalyType || '无异常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="异常天数" align="center" prop="anomalyDays" width="100">
        <template slot-scope="scope">
          {{ calculateAnomalyDays(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="入职日期" align="center" prop="hiredDate" />
      <el-table-column label="离职日期" align="center" prop="lastWorkDay" />

    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listEmployeeWork,deptTreeSelect } from "@/api/dingtalk/employeeWork";

import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "EmployeeWork",
  components: { Treeselect },
  dicts: ['emp_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 员工工作统计表格数据
      employeeWorkList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 部门树选项
      deptOptions: undefined,
      daterangeBatchNo: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        jobNumber: null,
        deptId: undefined,
        isAsc:"descending",
        orderByColumn:"job_number"
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    /** 查询员工工作统计列表 */
    getList() {
      this.loading = true;

      this.queryParams.params = {};
      if (null != this.daterangeBatchNo && '' != this.daterangeBatchNo) {
          this.queryParams.params["beginBatchNo"] = this.daterangeBatchNo[0];
          this.queryParams.params["endBatchNo"] = this.daterangeBatchNo[1];
      }

      if (null != this.queryParams.deptId && '' != this.queryParams.deptId) {
        this.queryParams.params["deptId"] = this.queryParams.deptId;
      }
      //如果报错了给空
      listEmployeeWork(this.queryParams).then(response => {
        this.employeeWorkList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(error => {
        // 如果报错了给空值
        this.employeeWorkList = [];
        this.total = 0;
        this.loading = false;
      })
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangeBatchNo = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.jobNumber)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 导出按钮操作 */
    handleExport() {
      this.queryParams.params = {};
      if (null != this.daterangeBatchNo && '' != this.daterangeBatchNo) {
        this.queryParams.params["beginBatchNo"] = this.daterangeBatchNo[0];
        this.queryParams.params["endBatchNo"] = this.daterangeBatchNo[1];
      }

      if (null != this.queryParams.deptId && '' != this.queryParams.deptId) {
        this.queryParams.params["deptId"] = this.queryParams.deptId;
      }


      this.download('dingtalk/employeeWork/export', {
        ...this.queryParams
      }, `employeeWork_${new Date().getTime()}.xlsx`)
    },

    /** 根据异常类型返回标签样式 */
    getAnomalyTagType(anomalyType) {
      switch(anomalyType) {
        case '无异常':
          return 'success';
        case '轻度异常':
          return 'warning';
        case '中度异常':
          return 'danger';
        case '重度异常':
          return 'danger';
        default:
          return 'info';
      }
    },

    /** 计算异常天数 */
    calculateAnomalyDays(row) {
      const ywcounts = parseFloat(row.ywcounts) || 0;
      const lateworkUndertime = parseFloat(row.lateworkUndertime) || 0;
      const earlyworkUndertime = parseFloat(row.earlyworkUndertime) || 0;
      const total = ywcounts + lateworkUndertime + earlyworkUndertime;
      return total.toFixed(1);
    }
  }
};
</script>
