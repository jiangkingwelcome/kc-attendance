import request from '@/utils/request'

// 查询员工考勤记录列表
export function listAttendance(query) {
  return request({
    url: '/dingtalk/attendance/list',
    method: 'get',
    params: query
  })
}

// 查询员工考勤记录详细
export function getAttendance(id) {
  return request({
    url: '/dingtalk/attendance/' + id,
    method: 'get'
  })
}

// 新增员工考勤记录
export function addAttendance(data) {
  return request({
    url: '/dingtalk/attendance',
    method: 'post',
    data: data
  })
}

// 修改员工考勤记录
export function updateAttendance(data) {
  return request({
    url: '/dingtalk/attendance',
    method: 'put',
    data: data
  })
}

// 删除员工考勤记录
export function delAttendance(id) {
  return request({
    url: '/dingtalk/attendance/' + id,
    method: 'delete'
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/dingtalk/attendance/deptTree',
    method: 'get'
  })
}
