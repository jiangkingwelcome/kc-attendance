<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工id" prop="employeeId">
        <el-input
          v-model="queryParams.employeeId"
          placeholder="请输入员工id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="员工名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入员工名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门" prop="deptId">
        <treeselect style="width: 400px" v-model="queryParams.deptId" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
      </el-form-item>
      <el-form-item label="打卡时间">
        <el-date-picker
          v-model="daterangeUserCheckTime"
          style="width: 360px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
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
          v-hasPermi="['dingtalk:attendance:add']"
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
          v-hasPermi="['dingtalk:attendance:edit']"
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
          v-hasPermi="['dingtalk:attendance:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dingtalk:attendance:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="attendanceList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
      <el-table-column label="员工id" align="center" prop="employeeId" />
      <el-table-column label="员工名称" align="center" prop="name" />
      <el-table-column label="打卡时间" align="center" prop="userCheckTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.userCheckTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="考勤地点" align="center" prop="place"/>
      <el-table-column label="日期批次" align="center" prop="batchNo"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
<!--          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dingtalk:attendance:edit']"
          >修改</el-button>-->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dingtalk:attendance:remove']"
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
    <!-- 添加或修改员工考勤记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="员工id" prop="employeeId">
          <el-input v-model="form.employeeId" placeholder="请输入员工id" />
        </el-form-item>
        <el-form-item label="员工名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入员工名称" />
        </el-form-item>
        <el-form-item label="打卡时间" prop="userCheckTime">
          <el-date-picker clearable
                          v-model="form.userCheckTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择打卡时间"
                          style="width: 380px">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="公司" prop="companyType">
          <el-select v-model="form.companyType" placeholder="请选择公司" style="width: 380px">
            <el-option
              v-for="dict in dict.type.company_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考勤类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择考勤类型" style="width: 380px">
            <el-option
              v-for="dict in dict.type.attendance_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
       </el-form-item>
        <el-form-item label="打卡地点" prop="place">
           <el-input v-model="form.place" placeholder="请输入打卡地点" />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改员工考勤记录对话框 -->
    <el-dialog :title="title" :visible.sync="updateOpen" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="员工id" prop="employeeId">
          <el-input  v-model="form.employeeId" placeholder="请输入员工id" disabled/>
        </el-form-item>
        <el-form-item label="员工名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入员工名称" disabled />
        </el-form-item>
        <el-form-item label="打卡时间" prop="userCheckTime" >
          <el-date-picker clearable
                          v-model="form.userCheckTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择打卡时间"
                          style="width: 380px"
                          disabled
                          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="公司" prop="companyType">
          <el-select v-model="form.companyType" placeholder="请选择公司" style="width: 380px" disabled >
            <el-option
              v-for="dict in dict.type.company_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考勤类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择考勤类型" style="width: 380px" disabled>
            <el-option
              v-for="dict in dict.type.attendance_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="打卡地点" prop="place">
          <el-input v-model="form.place" placeholder="请输入打卡地点" />
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
import { listAttendance, getAttendance, delAttendance, addAttendance, updateAttendance,deptTreeSelect } from "@/api/dingtalk/attendance";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Attendance",
  dicts: ['attendance_data_type','company_type','attendance_type'],
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
      // 员工考勤记录表格数据
      attendanceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //修改弹出层
      updateOpen:false,
      // 部门树选项
      deptOptions: undefined,
      // 时间范围
      daterangeUserCheckTime: [],
      // 时间范围
      daterangeBatchNo: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        employeeId: null,
        name: null,
        deptId:undefined,
        isAsc:"descending",
        orderByColumn:"batch_no desc,employee_id desc,user_check_time",
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        employeeId: [
          { required: true, message: "员工id不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "员工名称不能为空", trigger: "blur" }
        ],
        place: [
          { required: true, message: "打卡地点不能为空", trigger: "blur" }
        ],
        userCheckTime: [
          { required: true, message: "员工打卡时间不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "考勤类型不能为空", trigger: "change" }
        ],
        companyType: [
          { required: true, message: "公司不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getDeptTree();
  },
  methods: {
    /** 查询员工考勤记录列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeUserCheckTime && '' != this.daterangeUserCheckTime) {
         this.queryParams.params["beginUserCheckTime"] = this.daterangeUserCheckTime[0];
         this.queryParams.params["endUserCheckTime"] = this.daterangeUserCheckTime[1];
      }
      if (null != this.daterangeBatchNo && '' != this.daterangeBatchNo) {
         this.queryParams.params["beginBatchNo"] = this.daterangeBatchNo[0];
         this.queryParams.params["endBatchNo"] = this.daterangeBatchNo[1];
      }
      if (null != this.queryParams.deptId && '' != this.queryParams.deptId) {
         this.queryParams.params["deptId"] = this.queryParams.deptId;
      }
      listAttendance(this.queryParams).then(response => {
        this.attendanceList = response.rows;
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
      this.updateOpen = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        employeeId: null,
        name: null,
        userCheckTime: null,
        batchNo: null,
        type: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        companyType: null
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
      this.daterangeUserCheckTime = [];
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
      this.title = "添加员工考勤记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAttendance(id).then(response => {
        this.form = response.data;
        this.updateOpen = true;
        this.title = "修改员工考勤记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAttendance(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.updateOpen = false;
              this.getList();
            });
          } else {
            addAttendance(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除员工考勤记录编号为"' + ids + '"的数据项？').then(function() {
        return delAttendance(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('dingtalk/attendance/export', {
        ...this.queryParams
      }, `attendance_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
