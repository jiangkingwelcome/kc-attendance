import request from '@/utils/request'

// 获取首页统计数据
export function getDashboardStats() {
  return request({
    url: '/dingtalk/stats/dashboard',
    method: 'get'
  })
}

// 刷新统计数据缓存
export function refreshStats() {
  return request({
    url: '/dingtalk/stats/refresh',
    method: 'get'
  })
}
