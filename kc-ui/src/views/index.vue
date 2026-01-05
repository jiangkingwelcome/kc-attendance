<template>
  <div class="dashboard-container">
    <!-- Welcome Banner with Glassmorphism -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">{{ greeting }}</h2>
        <p class="welcome-subtitle">智造未来，快人一步。今天是 {{ currentDate }}</p>
      </div>
      <div class="welcome-img">
        <img src="@/assets/images/login-background.jpg" style="opacity:0.2; position:absolute; right:0; top:-50px; width:600px;">
      </div>
    </div>

    <!-- Key Metrics Cards -->
    <el-row :gutter="20" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-people">
            <svg-icon icon-class="peoples" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">在岗职工</div>
            <count-to :start-val="0" :end-val="activeEmployeeCount" :duration="2000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-time">
            <svg-icon icon-class="time-range" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">考勤记录</div>
            <count-to :start-val="0" :end-val="attendanceCount" :duration="2000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-post">
            <svg-icon icon-class="server" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">系统账号</div>
            <count-to :start-val="0" :end-val="systemAccountCount" :duration="2000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-hire">
            <svg-icon icon-class="user" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">本月新入职</div>
            <count-to :start-val="0" :end-val="newHireCount" :duration="2000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts & Quick Actions -->
    <el-row :gutter="20">
      <el-col :span="18">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>近七日考勤趋势</span>
            <el-button style="float: right; padding: 3px 0" type="text" class="text-blue">查看详情</el-button>
          </div>
          <div id="chartLine" style="width:100%; height:350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="action-card">
          <div slot="header">
            <span>快捷入口</span>
          </div>
          <div class="action-list">
            <div class="action-item" v-for="item in quickActions" :key="item.name">
              <div class="action-icon" :style="{background: item.bg}">
                <i :class="item.icon"></i>
              </div>
              <span class="action-name">{{ item.name }}</span>
            </div>
          </div>
        </el-card>

         <el-card class="action-card" style="margin-top:20px;">
          <div slot="header">
            <span>系统公告</span>
          </div>
          <div class="notice-list">
             <div class="notice-item">
               <span class="notice-tag">由通知</span>
               <span class="notice-title">春节放假安排通知...</span>
             </div>
             <div class="notice-item">
               <span class="notice-tag warning">重要</span>
               <span class="notice-title">考勤系统升级维护...</span>
             </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import echarts from 'echarts'
import { listUser } from '@/api/system/user'
import { listAttendance } from '@/api/dingtalk/attendance'
import { listEmployee } from '@/api/dingtalk/employee'

export default {
  name: 'Index',
  components: {
    CountTo
  },
  data() {
    return {
      currentDate: new Date().toLocaleDateString(),
      greeting: '',
      activeEmployeeCount: 0,
      attendanceCount: 0,
      systemAccountCount: 0,
      newHireCount: 0,
      quickActions: [
        { name: '员工管理', icon: 'el-icon-user', bg: '#E0F2FE' },
        { name: '考勤补卡', icon: 'el-icon-time', bg: '#DCFCE7' },
        { name: '请假审批', icon: 'el-icon-document-checked', bg: '#FEF3C7' },
        { name: '考勤报表', icon: 'el-icon-data-line', bg: '#FEE2E2' },
        { name: '排班管理', icon: 'el-icon-date', bg: '#F3E8FF' },
        { name: '系统设置', icon: 'el-icon-setting', bg: '#E5E7EB' }
      ]
    }
  },
  mounted() {
    this.updateGreeting();
    this.initChart();
    this.getStats();
  },
  methods: {
    getStats() {
       // 1. Fetch Active Employees (DingTalk Source)
       // Filter client-side by 'lastWorkDay' (Resignation Date)
       listEmployee({ pageNum: 1, pageSize: 10000 }).then(response => {
           const allEmployees = response.rows;
           // Count employees who have NO resignation date
           const activeEmployees = allEmployees.filter(emp => !emp.lastWorkDay);
           this.activeEmployeeCount = activeEmployees.length;
       });
       
       // 2. Fetch Attendance Records
       listAttendance({ pageNum: 1, pageSize: 1 }).then(response => {
           this.attendanceCount = response.total; 
       });
       
       // 3. Fetch System Accounts (The 1000+ users)
       listUser().then(response => {
           this.systemAccountCount = response.total;
       });
       
       // 4. Fetch New Hires (From Employee List)
       const now = new Date();
       const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
       const beginTime = startOfMonth.toISOString().split('T')[0];
       
       const query = {
           pageNum: 1,
           pageSize: 10000,
           params: {
               beginTime: beginTime
           }
       };
       
       listEmployee(query).then(response => {
           this.newHireCount = response.total;
       });
    },
    updateGreeting() {
      const hour = new Date().getHours();
      if (hour >= 5 && hour < 12) {
        this.greeting = '早安，管理员 ☀️';
      } else if (hour >= 12 && hour < 14) {
        this.greeting = '午安，管理员 ☕';
      } else if (hour >= 14 && hour < 18) {
        this.greeting = '下午好，管理员 🥤';
      } else if (hour >= 18 && hour < 22) {
        this.greeting = '晚上好，管理员 🌙';
      } else {
        this.greeting = '夜深了，注意休息 💤';
      }
    },
    initChart() {
      const chart = echarts.init(document.getElementById('chartLine'))
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
           left: '3%',
           right: '4%',
           bottom: '3%',
           containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          axisLine: { lineStyle: { color: '#9CA3AF' } }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { type: 'dashed', color: '#E5E7EB' } }
        },
        series: [{
          data: [1150, 1180, 1190, 1170, 1185, 200, 150],
          type: 'line',
          smooth: true,
          itemStyle: { color: '#3875F6' },
          areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: 'rgba(56, 117, 246, 0.3)'
              }, {
                  offset: 1,
                  color: 'rgba(56, 117, 246, 0)'
              }])
          }
        }]
      }
      chart.setOption(option)
      window.addEventListener("resize", () => { chart.resize();});
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 24px;
  background-color: #F3F4F6;
  min-height: calc(100vh - 84px);

  .welcome-banner {
    background: linear-gradient(135deg, #3875F6 0%, #2563EB 100%);
    border-radius: 12px;
    padding: 30px;
    color: #fff;
    margin-bottom: 24px;
    position: relative;
    overflow: hidden;
    box-shadow: 0 10px 20px rgba(56, 117, 246, 0.2);

    h2 {
      margin: 0 0 10px 0;
      font-size: 28px;
    }
    p {
      margin: 0;
      opacity: 0.9;
      font-size: 16px;
    }
  }

  .panel-group {
    margin-bottom: 24px;
    .card-panel-col {
      margin-bottom: 24px;
    }
    .card-panel {
      height: 108px;
      cursor: pointer;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      color: #666;
      background: #fff;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      border-radius: 12px;
      display: flex;
      align-items: center;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        
        .card-panel-icon-wrapper {
          color: #fff;
        }
        .icon-people { background: #40c9c6; }
        .icon-time { background: #36a3f7; }
        .icon-post { background: #A855F7; }
        .icon-hire { background: #10B981; }
      }

      .card-panel-icon-wrapper {
        float: left;
        margin: 14px 0 0 14px;
        padding: 16px;
        transition: all 0.38s ease-out;
        border-radius: 6px;
        font-size: 48px;
      }

      .card-panel-icon {
        float: left;
        font-size: 48px;
      }
      
      .icon-people { color: #40c9c6; }
      .icon-time { color: #36a3f7; }
      .icon-post { color: #A855F7; }
      .icon-hire { color: #10B981; }

      .card-panel-description {
        float: right;
        font-weight: bold;
        margin: 26px;
        margin-left: 20px;

        .card-panel-text {
          line-height: 18px;
          color: rgba(0, 0, 0, 0.45);
          font-size: 16px;
          margin-bottom: 12px;
        }

        .card-panel-num {
          font-size: 24px;
          color: #111827;
        }
        
        .text-danger {
            color: #EF4444;
        }
      }
    }
  }

  .chart-card {
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      border: none;
      
      .clearfix {
          font-size: 16px;
          font-weight: 600;
          color: #111827;
      }
  }
  
  .action-card {
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      border: none;
      
      .action-list {
          display: flex;
          flex-wrap: wrap;
          justify-content: space-between;
      }
      .action-item {
          width: 30%;
          text-align: center;
          margin-bottom: 15px;
          cursor: pointer;
          
          &:hover {
               .action-icon {
                   transform: scale(1.1);
               }
               span {
                   color: #3875F6;
               }
          }
          
          .action-icon {
              width: 45px;
              height: 45px;
              border-radius: 12px;
              display: flex;
              align-items: center;
              justify-content: center;
              margin: 0 auto 8px auto;
              transition: all 0.3s;
              
              i {
                  font-size: 20px;
                  color: #3875F6; // Default to brand color for simplicity, or use dynamic
                  // Actually let's use darker text for icon
                  color: #374151; 
              }
          }
          
          .action-name {
              font-size: 12px;
              color: #6B7280;
          }
      }
  }
  
  .notice-list {
      .notice-item {
          padding: 10px 0;
          border-bottom: 1px solid #F3F4F6;
          display: flex;
          align-items: center;
          
          &:last-child {
              border-bottom: none;
          }
          
          .notice-tag {
              padding: 2px 6px;
              background: #EFF6FF;
              color: #3875F6;
              font-size: 12px;
              border-radius: 4px;
              margin-right: 10px;
              
              &.warning {
                  background: #FEF2F2;
                  color: #EF4444;
              }
          }
          
          .notice-title {
              font-size: 14px;
              color: #374151;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
          }
      }
  }
}
</style>

