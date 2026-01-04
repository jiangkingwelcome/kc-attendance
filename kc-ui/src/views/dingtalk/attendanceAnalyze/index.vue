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

<!--      <el-form-item label="员工id" prop="employeeId">
        <el-input
          v-model="queryParams.employeeId"
          placeholder="请输入员工id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->

      <el-form-item label="工号" prop="jobNumber">
        <el-input
          v-model="queryParams.jobNumber"
          placeholder="请输入工号"
          clearable
          @keyup.enter.native="handleQuery"
          style="width:150px"
        />
      </el-form-item>

      <el-form-item label="日期批次">
        <el-date-picker
          v-model="daterangeBatchNo"
          style="width: 240px"
          value-format="yyyyMMdd"
          format = "yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>

      <el-form-item label="部门" prop="deptId">
        <treeselect style="width: 400px" v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable   style="width:150px">
          <el-option
            v-for="dict in dict.type.dt_attendance_analyze_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['dingtalk:attendanceAnalyze:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['dingtalk:attendanceAnalyze:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['dingtalk:attendanceAnalyze:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dingtalk:attendanceAnalyze:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="attendanceAnalyzeList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
<!--      <el-table-column label="员工名称" align="center" width="100">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tooltip :content="scope.row.employeeId" placement="right" effect="light">-->
<!--			<span style="-->
<!--                    display: -webkit-box;-->
<!--                    text-overflow: ellipsis;-->
<!--                    overflow: hidden;-->
<!--                    -webkit-line-clamp: 2;-->
<!--                    -webkit-box-orient: vertical;-->
<!--                    white-space: pre-line;-->
<!--                    ">-->
<!--                    {{ scope.row.name }}-->
<!--                </span>-->
<!--          </el-tooltip>-->
<!--        </template>-->
<!--      </el-table-column>-->


      <el-table-column label="姓名" align="center" prop="name" width="130"/>
      <el-table-column label="工号" align="center" prop="jobNumber" width="120"/>

      <el-table-column label="人员类型" align="center" prop="empType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.emp_type" :value="scope.row.empType"/>
        </template>
      </el-table-column>
      <el-table-column label="员工id" align="center" prop="employeeId" width="150"/>
      <el-table-column label="上班时间" align="center" prop="workTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.workTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下班时间" align="center" prop="offWorkTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.offWorkTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="日期批次" align="center" prop="batchNo" width="100"/>
      <el-table-column label="上班地点|下班地点" align="center" prop="place" width="150"/>
      <el-table-column label="工时" align="center" prop="duration" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.dt_attendance_analyze_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="请假记录" align="center" prop="leaveRecord" width="400">
        <template slot-scope="scope">
          <span v-html="scope.row.leaveRecord"></span>
        </template>
      </el-table-column>
      <el-table-column label="请假时长(天)" align="center" prop="leaveDuration" width="400">
        <template slot-scope="scope">
          <span v-html="scope.row.leaveDuration"></span>
        </template>
      </el-table-column>
      <el-table-column label="请假次数" align="center" prop="leaveCount" width="400">
        <template slot-scope="scope">
          <span v-html="scope.row.leaveCount"></span>
        </template>
      </el-table-column>
      <el-table-column label="加班时长(小时)" align="center" prop="overtimeDurationHour" width="150">
        <template slot-scope="scope">
          <span v-html="scope.row.overtimeDurationHour"></span>
        </template>
      </el-table-column>
      <el-table-column label="加班时长(天)" align="center" prop="overtimeDurationDay" width="150">
        <template slot-scope="scope">
          <span v-html="scope.row.overtimeDurationDay"></span>
        </template>
      </el-table-column>
      <el-table-column label="出差记录" align="center" prop="travelRecord" width="400">
        <template slot-scope="scope">
          <span v-html="scope.row.travelRecord"></span>
        </template>
      </el-table-column>
      <el-table-column label="出差时长(小时)" align="center" prop="travelDuration" width="400">
        <template slot-scope="scope">
          <span v-html="scope.row.travelDuration"></span>
        </template>
      </el-table-column>
      <el-table-column label="出差次数" align="center" prop="travelCount" width="400">
        <template slot-scope="scope">
          <span v-html="scope.row.travelCount"></span>
        </template>
      </el-table-column>
      <el-table-column label="入职日期" align="center" prop="hiredDate" />
      <el-table-column label="离职日期" align="center" prop="lastWorkDay" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dingtalk:attendanceAnalyze:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dingtalk:attendanceAnalyze:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改员工考勤汇总分析对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="员工id" prop="employeeId">
          <el-input v-model="form.employeeId" placeholder="请输入员工id" />
        </el-form-item>
        <el-form-item label="员工名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入员工名称" />
        </el-form-item>
        <el-form-item label="上班时间" prop="workTime">
          <el-date-picker clearable
            v-model="form.workTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择上班时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="下班时间" prop="offWorkTime">
          <el-date-picker clearable
            v-model="form.offWorkTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择下班时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="日期批次" prop="batchNo">
          <el-input v-model="form.batchNo" placeholder="请输入日期批次" />
        </el-form-item>
        <el-form-item label="工作时长" prop="duration">
          <el-input v-model="form.duration" placeholder="请输入工作时长" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.dt_attendance_analyze_status"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAttendanceAnalyze, getAttendanceAnalyze, delAttendanceAnalyze, addAttendanceAnalyze, updateAttendanceAnalyze,deptTreeSelect } from "@/api/dingtalk/attendanceAnalyze";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "AttendanceAnalyze",
  dicts: ['dt_attendance_analyze_status','overtime_type','emp_type'],
  components: { Treeselect },
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
      // 员工考勤汇总分析表格数据
      attendanceAnalyzeList: [],
      // 弹出层标题
      title: "",
      // 部门树选项
      deptOptions: undefined,
      daterangeBatchNo: [],
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        employeeId: null,
        name: null,
        workTime: null,
        offWorkTime: null,
        batchNo: null,
        duration: null,
        status: null,
        batchNoArray:null,
        isAsc:"descending",
        orderByColumn:"batch_no desc,employee_id desc,work_time",
        deptId: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        employeeId: [
          { required: true, message: "员工id不能为空", trigger: "blur" }
        ],
        duration: [
          { required: true, message: "工作时长不能为空", trigger: "blur" }
        ]
      },
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      }
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    /** 查询员工考勤汇总分析列表 */
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


      listAttendanceAnalyze(this.queryParams).then(response => {
        this.attendanceAnalyzeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        employeeId: null,
        name: null,
        workTime: null,
        offWorkTime: null,
        batchNo: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        duration: null,
        status: 0
      };
      this.resetForm("form");
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加员工考勤汇总分析";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAttendanceAnalyze(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改员工考勤汇总分析";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAttendanceAnalyze(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAttendanceAnalyze(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除员工考勤汇总分析编号为"' + ids + '"的数据项？').then(function() {
        return delAttendanceAnalyze(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
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
      this.download('dingtalk/attendanceAnalyze/export', {
        ...this.queryParams
      }, `attendanceAnalyze_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
