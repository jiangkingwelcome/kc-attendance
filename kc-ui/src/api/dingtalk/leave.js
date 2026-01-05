import request from '@/utils/request'

// 查询今日请假人数
export function getTodayLeaveCount() {
  return request({
    url: '/dingtalk/leave/todayCount',
    method: 'get'
  })
}
