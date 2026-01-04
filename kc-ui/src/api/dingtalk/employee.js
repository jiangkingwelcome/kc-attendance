import request from '@/utils/request'

// 查询钉钉员工基础信息列表
export function listEmployee(query) {
  return request({
    url: '/dingtalk/employee/list',
    method: 'get',
    params: query
  })
}

// 查询钉钉员工基础信息详细
export function getEmployee(id) {
  return request({
    url: '/dingtalk/employee/' + id,
    method: 'get'
  })
}

// 新增钉钉员工基础信息
export function addEmployee(data) {
  return request({
    url: '/dingtalk/employee',
    method: 'post',
    data: data
  })
}

// 修改钉钉员工基础信息
export function updateEmployee(data) {
  return request({
    url: '/dingtalk/employee',
    method: 'put',
    data: data
  })
}

// 删除钉钉员工基础信息
export function delEmployee(id) {
  return request({
    url: '/dingtalk/employee/' + id,
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
