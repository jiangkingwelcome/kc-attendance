import request from '@/utils/request'

// 查询员工考勤汇总分析列表
export function listAttendanceAnalyze(query) {
  return request({
    url: '/dingtalk/attendanceAnalyze/list',
    method: 'get',
    params: query
  })
}

// 查询员工考勤汇总分析详细
export function getAttendanceAnalyze(id) {
  return request({
    url: '/dingtalk/attendanceAnalyze/' + id,
    method: 'get'
  })
}

// 新增员工考勤汇总分析
export function addAttendanceAnalyze(data) {
  return request({
    url: '/dingtalk/attendanceAnalyze',
    method: 'post',
    data: data
  })
}

// 修改员工考勤汇总分析
export function updateAttendanceAnalyze(data) {
  return request({
    url: '/dingtalk/attendanceAnalyze',
    method: 'put',
    data: data
  })
}

// 删除员工考勤汇总分析
export function delAttendanceAnalyze(id) {
  return request({
    url: '/dingtalk/attendanceAnalyze/' + id,
    method: 'delete'
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/dingtalk/attendanceAnalyze/deptTree',
    method: 'get'
  })
}
