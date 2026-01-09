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
      <el-col :xs="12" :sm="12" :lg="8" class="card-panel-col">
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
      <el-col :xs="12" :sm="12" :lg="8" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-leave">
            <svg-icon icon-class="exit-fullscreen" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日请假人数</div>
            <count-to :start-val="0" :end-val="todayLeaveCount" :duration="2000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="8" class="card-panel-col">
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
            <div class="action-item" v-for="item in quickActions" :key="item.name" @click="handleQuickAction(item)">
              <div class="action-icon" :style="{background: item.bg}">
                <i :class="item.icon"></i>
              </div>
              <span class="action-name">{{ item.name }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Abnormal Stats Chart -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>近七日考勤异常趋势</span>
            <el-tag size="small" type="danger" style="margin-left: 10px;">异常包含：缺卡、迟到、早退</el-tag>
            <el-button style="float: right; padding: 3px 0" type="text" class="text-blue" @click="$router.push('/dingtalk/attendanceAnalyze')">查看详情</el-button>
          </div>
          <div id="chartAbnormal" style="width:100%; height:350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import echarts from 'echarts'
import request from '@/utils/request'

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
      todayLeaveCount: 0,
      newHireCount: 0,
      chartData: {
        dates: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        counts: [0, 0, 0, 0, 0, 0, 0]
      },
      abnormalChartData: {
        dates: [],
        counts: [],
        fullData: [] // 保存完整的数据，包括dayType、hasAttendanceData等
      },
      quickActions: [
        { name: '员工设置', icon: 'el-icon-user', bg: '#E0F2FE', path: '/dingtalk/employee' },
        { name: '考勤汇总', icon: 'el-icon-data-line', bg: '#FEE2E2', path: '/dingtalk/attendanceAnalyze' },
        { name: '员工统计', icon: 'el-icon-s-data', bg: '#FEF3C7', path: '/dingtalk/employeeWork' },
        { name: '参数设置', icon: 'el-icon-setting', bg: '#F3E8FF', path: '/system/config' },
        { name: '用户管理', icon: 'el-icon-user-solid', bg: '#DCFCE7', path: '/system/user' },
        { name: '角色管理', icon: 'el-icon-s-custom', bg: '#E5E7EB', path: '/system/role' }
      ]
    }
  },
  mounted() {
    this.updateGreeting();
    this.getStats();
  },
  methods: {
    handleQuickAction(item) {
      if (item.path) {
        this.$router.push(item.path);
      }
    },
    getStats() {
       // 使用Redis缓存的统计数据API，一次性获取所有统计数据
       request({
         url: '/dingtalk/stats/dashboard',
         method: 'get'
       }).then(response => {
         this.activeEmployeeCount = response.data.activeEmployeeCount;
         this.attendanceCount = response.data.attendanceCount;
         this.todayLeaveCount = response.data.todayLeaveCount;
         this.newHireCount = response.data.newHireCount;

         // 处理近七日考勤数据
         if (response.data.last7DaysAttendance && response.data.last7DaysAttendance.length > 0) {
           this.chartData.dates = response.data.last7DaysAttendance.map(item => item.day || item.date);
           this.chartData.counts = response.data.last7DaysAttendance.map(item => item.count || 0);
         }

         // 处理近七日考勤异常数据
         if (response.data.last7DaysAbnormal && response.data.last7DaysAbnormal.length > 0) {
           this.abnormalChartData.fullData = response.data.last7DaysAbnormal; // 保存完整数据
           this.abnormalChartData.dates = response.data.last7DaysAbnormal.map(item => item.day || item.date);
           this.abnormalChartData.counts = response.data.last7DaysAbnormal.map(item => item.abnormalCount || 0);
         }

         // 初始化图表
         this.initChart();
         this.initAbnormalChart();
       }).catch(error => {
         console.error('获取统计数据失败:', error);
         // 即使出错也初始化图表（使用默认数据）
         this.initChart();
         this.initAbnormalChart();
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
          trigger: 'axis',
          formatter: function(params) {
            const data = params[0];
            return `日期: ${data.axisValue}<br/>打卡次数: ${data.value} 次`;
          }
        },
        grid: {
           left: '3%',
           right: '4%',
           bottom: '3%',
           containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.chartData.dates,
          name: '日期',
          nameTextStyle: { color: '#6B7280', fontSize: 12 },
          axisLine: { lineStyle: { color: '#9CA3AF' } }
        },
        yAxis: {
          type: 'value',
          name: '打卡次数',
          nameTextStyle: { color: '#6B7280', fontSize: 12 },
          axisLine: { show: false },
          splitLine: { lineStyle: { type: 'dashed', color: '#E5E7EB' } }
        },
        series: [{
          name: '考勤打卡',
          data: this.chartData.counts,
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
    },
    initAbnormalChart() {
      const chart = echarts.init(document.getElementById('chartAbnormal'))
      const self = this; // 保存this引用
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            const dataIndex = params[0].dataIndex;
            const data = params[0];
            const fullDataItem = self.abnormalChartData.fullData[dataIndex] || {};

            let tips = [];
            tips.push(`日期: ${data.axisValue}`);

            // 显示星期几
            if (fullDataItem.weekDay) {
              const weekDays = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日'];
              tips.push(`星期: ${weekDays[parseInt(fullDataItem.weekDay)] || fullDataItem.weekDay}`);
            }

            // 显示节假日信息
            if (fullDataItem.holiday && fullDataItem.holiday !== '无') {
              tips.push(`节假日: ${fullDataItem.holiday}`);
            }

            tips.push(`异常数量: ${data.value} 人次`);

            // 显示异常明细
            if (data.value > 0) {
              const missingPunch = fullDataItem.missingPunch || 0;
              const lateOrEarly = fullDataItem.lateOrEarly || 0;

              tips.push('<span style="color:#94a3b8;">━━━━━━━━━━━━━━━━</span>');
              tips.push('<span style="font-weight:bold;">异常明细：</span>');
              if (missingPunch > 0) {
                tips.push(`　缺卡: ${missingPunch} 人次`);
              }
              if (lateOrEarly > 0) {
                tips.push(`　迟到/早退: ${lateOrEarly} 人次`);
              }
            }

            // 显示提示信息
            if (fullDataItem.dayType === '2') {
              tips.push('<span style="color:#f59e0b;">⚠ 该天为周末</span>');
            }

            if (fullDataItem.hasAttendanceData === false) {
              tips.push('<span style="color:#ef4444;">⚠ 数据可能还未更新</span>');
            }

            tips.push('<span style="color:#3875F6;">💡 点击柱状图查看详情</span>');

            return tips.join('<br/>');
          }
        },
        grid: {
           left: '3%',
           right: '4%',
           bottom: '3%',
           containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.abnormalChartData.dates.length > 0 ? this.abnormalChartData.dates : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          name: '日期',
          nameTextStyle: { color: '#6B7280', fontSize: 12 },
          axisLine: { lineStyle: { color: '#9CA3AF' } }
        },
        yAxis: {
          type: 'value',
          name: '异常人次',
          nameTextStyle: { color: '#6B7280', fontSize: 12 },
          axisLine: { show: false },
          splitLine: { lineStyle: { type: 'dashed', color: '#E5E7EB' } }
        },
        series: [{
          name: '考勤异常',
          data: this.abnormalChartData.counts.length > 0 ? this.abnormalChartData.counts : [0, 0, 0, 0, 0, 0, 0],
          type: 'bar',
          itemStyle: {
            // 动态设置颜色：没有打卡数据的显示灰色，有数据的显示红色渐变
            color: (params) => {
              const fullDataItem = self.abnormalChartData.fullData[params.dataIndex] || {};
              if (fullDataItem.hasAttendanceData === false && params.value > 0) {
                // 没有打卡数据，显示橙色警告色
                return new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: '#fbbf24'
                }, {
                  offset: 1,
                  color: '#f59e0b'
                }]);
              } else {
                // 有打卡数据，显示红色
                return new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: '#f87171'
                }, {
                  offset: 1,
                  color: '#ef4444'
                }]);
              }
            },
            borderRadius: [4, 4, 0, 0]
          },
          barWidth: '60%',
          label: {
            show: true,
            position: 'top',
            formatter: (params) => {
              const fullDataItem = self.abnormalChartData.fullData[params.dataIndex] || {};
              // 如果没有打卡数据且有异常，显示"未更新"
              if (fullDataItem.hasAttendanceData === false && params.value > 0) {
                return `${params.value}\n未更新`;
              }
              return params.value;
            },
            color: '#6B7280',
            fontSize: 11
          }
        }]
      }
      chart.setOption(option)

      // 添加点击事件
      chart.on('click', function(params) {
        if (params.componentType === 'series') {
          const dataIndex = params.dataIndex;
          const fullDataItem = self.abnormalChartData.fullData[dataIndex] || {};
          const clickedDate = params.name; // 获取点击的日期，格式如 "01-04"

          // 将日期格式转换为 yyyyMMdd 格式
          const dateStr = self.convertDateFormat(clickedDate);

          // 跳转到员工工作统计页面，并传递日期参数
          self.$router.push({
            path: '/dingtalk/employeeWork',
            query: {
              date: dateStr
            }
          });
        }
      });

      window.addEventListener("resize", () => { chart.resize();});
    },

    // 将日期格式从 "MM-DD" 转换为 "yyyyMMdd"
    convertDateFormat(dateStr) {
      // dateStr 格式如 "01-04"
      const currentYear = new Date().getFullYear();
      const [month, day] = dateStr.split('-');
      return `${currentYear}${month}${day}`;
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
        .icon-leave { background: #f4516c; }
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
      .icon-leave { color: #f4516c; }
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
}
</style>

