import request from '@/utils/request'

// 查询员工工作统计列表
export function listEmployeeWork(query) {
  return request({
    url: '/dingtalk/employeeWork/list',
    method: 'get',
    params: query
  })
}

// 查询员工工作统计详细
export function getEmployeeWork(jobNumber) {
  return request({
    url: '/dingtalk/employeeWork/' + jobNumber,
    method: 'get'
  })
}

// 新增员工工作统计
export function addEmployeeWork(data) {
  return request({
    url: '/dingtalk/employeeWork',
    method: 'post',
    data: data
  })
}

// 修改员工工作统计
export function updateEmployeeWork(data) {
  return request({
    url: '/dingtalk/employeeWork',
    method: 'put',
    data: data
  })
}

// 删除员工工作统计
export function delEmployeeWork(jobNumber) {
  return request({
    url: '/dingtalk/employeeWork/' + jobNumber,
    method: 'delete'
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/dingtalk/employeeWork/deptTree',
    method: 'get'
  })
}
