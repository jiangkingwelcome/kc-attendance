# KC 考勤管理平台

## 项目简介

KC 是一款基于钉钉开放平台的企业考勤管理系统，采用前后端分离架构，支持考勤数据统计、分析、报表生成等功能。

**版本**: 3.8.5（后端）/ 3.8.4（前端）

---

## 技术栈

### 后端（Spring Boot）
- **框架**: Spring Boot 2.5.14
- **ORM**: MyBatis 2.2.2 + PageHelper 分页
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **连接池**: Druid（阿里）
- **认证**: JWT + Spring Security
- **定时任务**: Quartz
- **集成**: 钉钉开放平台 API 2.0.43
- **API 文档**: Swagger 3.0.0

### 前端（Vue）
- **框架**: Vue 2.6.12
- **UI 库**: Element UI 2.15.10
- **状态管理**: Vuex 3.6.0
- **路由**: Vue Router 3.4.9
- **HTTP**: Axios 0.24.0
- **可视化**: ECharts 4.9.0
- **编辑器**: Quill 1.3.7

---

## 项目结构

```
kc/
├── kc-master/                  # 后端主项目
│   ├── kc-admin/              # 管理后台应用
│   ├── kc-framework/          # 核心框架（配置、安全、AOP等）
│   ├── kc-system/             # 系统管理模块（用户、权限、菜单等）
│   ├── kc-common/             # 通用工具（常量、工具类、异常等）
│   ├── kc-dingtalk/           # 钉钉集成模块（数据同步、考勤管理）
│   ├── kc-quartz/             # 定时任务调度
│   ├── kc-generator/          # 代码生成工具
│   ├── sql/                   # 数据库初始化脚本
│   └── pom.xml               # Maven 主 POM 文件
│
├── kc-ui/                      # 前端项目
│   ├── src/
│   │   ├── api/              # API 接口定义
│   │   ├── components/       # 可复用组件
│   │   ├── views/            # 业务页面
│   │   ├── store/            # Vuex 状态管理
│   │   ├── router/           # 路由配置
│   │   ├── utils/            # 工具函数
│   │   └── main.js           # 入口文件
│   └── package.json          # npm 依赖
│
├── .gitignore               # Git 忽略配置
├── README.md                # 本文件
└── GIT_SETUP.md             # Git 上传指南
```

---

## 快速开始

### 前置要求

- **Java 8+** (建议 Java 8 或 11)
- **Maven 3.6+**
- **Node.js 12+**
- **MySQL 8.0+**
- **Redis 5.0+**

### 1. 数据库初始化

```bash
# 创建数据库
CREATE DATABASE kc CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# 导入初始化脚本
mysql -u root -p kc < kc-master/sql/ry_20220822.sql
mysql -u root -p kc < kc-master/sql/quartz.sql
```

默认用户：
- 账号: `admin`
- 密码: `123456`

### 2. 配置数据库连接

编辑 `kc-master/kc-admin/src/main/resources/application-druid.yml`:

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/kc?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&allowMultiQueries=true
        username: root
        password: 123456  # 修改为实际密码
```

### 3. 启动后端服务

```bash
cd kc-master
mvn clean install
cd kc-admin
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8080`，API 文档: `http://localhost:8080/swagger-ui.html`

### 4. 启动前端服务

```bash
cd kc-ui
npm install
npm run dev
```

前端服务运行在 `http://localhost:8000`（如有冲突会自动调整）

### 5. 访问应用

打开浏览器访问 `http://localhost:8000`，用默认账号登录

---

## 核心功能

### 考勤管理
- ✅ 考勤数据导入/导出
- ✅ 出勤统计分析
- ✅ 异常考勤处理
- ✅ 考勤报表生成

### 系统管理
- ✅ 用户管理（增删改查、批量导入）
- ✅ 权限管理（RBAC 角色权限模型）
- ✅ 菜单管理
- ✅ 部门管理（树形结构）
- ✅ 岗位管理
- ✅ 字典管理

### 监控管理
- ✅ 在线用户监控
- ✅ 定时任务管理
- ✅ 数据库监控（Druid）
- ✅ 系统性能监控
- ✅ Redis 缓存监控
- ✅ 操作日志
- ✅ 登录日志

### 工具功能
- ✅ 代码生成（低代码快速开发）
- ✅ 表单构建
- ✅ API 文档（Swagger）

### 钉钉集成
- ✅ 部门同步
- ✅ 员工信息同步
- ✅ 考勤数据拉取
- ✅ 请假/加班/出差管理

---

## 配置说明

### 后端配置

**application.yml** 核心配置：
```yaml
kc:
  profile: D:/kc/uploadPath  # 文件上传路径
  captchaType: math          # 验证码类型（math/char）
  
server:
  port: 8080                 # 服务端口
  
spring:
  redis:
    host: localhost
    port: 6379
```

**application-druid.yml** 数据库配置：
```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/kc
        username: root
        password: 123456
```

### 前端环境变量

`.env.development` (开发环境)：
```bash
VUE_APP_BASE_API = http://localhost:8080
```

`.env.production` (生产环境)：
```bash
VUE_APP_BASE_API = http://api.example.com
```

---

## 常见问题

### Q: 如何修改默认端口？
A: 编辑 `kc-master/kc-admin/src/main/resources/application.yml` 的 `server.port`

### Q: 如何添加新的菜单和权限？
A: 
1. 在 `sys_menu` 表添加菜单记录
2. 使用代码生成工具快速生成 CRUD 代码
3. 在前端 `src/views` 添加对应页面

### Q: 如何集成钉钉？
A: 编辑 `application-dingtalk.yml`，配置企业 ID 和应用密钥

### Q: 前端报错 CORS？
A: 检查后端跨域配置，或修改前端 API 基础 URL

---

## 文件上传

- 默认上传路径: `D:/kc/uploadPath`（Windows）或 `/home/kc/uploadPath`（Linux）
- 最大单文件: 10MB
- 最大总大小: 20MB

修改路径编辑 `application.yml` 的 `kc.profile` 配置

---

## 数据库表

### 核心系统表
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_menu` - 菜单表
- `sys_dept` - 部门表
- `sys_post` - 岗位表
- `sys_user_role` - 用户角色关联表
- `sys_role_menu` - 角色菜单关联表
- `sys_dict_type` - 字典类型表
- `sys_dict_data` - 字典数据表

### 钉钉模块表
- `dt_attendance` - 考勤数据表
- `dt_employee` - 员工表
- `dt_leave` - 请假记录表
- `dt_overtime` - 加班记录表
- `dt_travel` - 出差记录表
- `dt_holiday` - 节假日表
- `dt_workday` - 工作日表

### 监控表
- `sys_oper_log` - 操作日志表
- `sys_logininfor` - 登录日志表
- `qrtz_*` - Quartz 定时任务表

---

## 开发指南

### 后端开发

#### 1. 创建新业务模块

创建数据库表 → 使用代码生成工具 → 生成 Entity、Mapper、Service、Controller

#### 2. 规范

- 命名: 驼峰法则 (userInfo)
- 注释: 使用 JavaDoc
- 日志: 使用 SLF4J
- 异常: 继承 BaseException
- 响应: 统一使用 AjaxResult

### 前端开发

#### 1. 新增页面

```javascript
// src/views/demo/index.vue
<template>
  <div class="app-container">
    <!-- 页面内容 -->
  </div>
</template>

<script>
export default {
  name: 'Demo'
}
</script>
```

#### 2. 新增 API

```javascript
// src/api/demo/index.js
import request from '@/utils/request'

export function getDemo() {
  return request({
    url: '/demo/list',
    method: 'get'
  })
}
```

#### 3. 路由配置

在 `src/router/index.js` 添加:
```javascript
{
  path: '/demo',
  component: Layout,
  children: [{
    path: 'index',
    component: () => import('@/views/demo/index'),
    name: 'Demo',
    meta: { title: '演示', icon: 'demo' }
  }]
}
```

---

## 部署

### Docker 部署

```bash
# 构建镜像
docker build -t kc-admin:3.8.5 .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_HOST=mysql \
  -e DB_PASSWORD=123456 \
  kc-admin:3.8.5
```

### 生产环境打包

**后端:**
```bash
cd kc-master/kc-admin
mvn clean package -DskipTests
# 生成 JAR: target/kc-admin.jar
```

**前端:**
```bash
cd kc-ui
npm run build:prod
# 生成静态文件: dist/
```

---

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 许可证

MIT License - 详见 LICENSE 文件

---

## 联系方式

- 文档: [Wiki](https://github.com/your-repo/wiki)
- 问题反馈: [Issues](https://github.com/your-repo/issues)
- 讨论区: [Discussions](https://github.com/your-repo/discussions)

---

## 更新日志

### v3.8.5 (2022-08-22)
- ✨ 初始版本发布
- 🎉 支持完整的考勤管理功能
- 🔗 集成钉钉开放平台
- 📊 提供数据分析和报表功能

---

**最后更新**: 2026-01-04
