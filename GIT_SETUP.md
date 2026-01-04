# Git 上传指南

## 项目大小分析

### 不上传的文件夹（已在 .gitignore 配置）

| 文件夹 | 大小 | 原因 |
|--------|------|------|
| `kc-ui/node_modules/` | ~220 MB | NPM 依赖包，可通过 `npm install` 重新生成 |
| `kc-master/*/target/` | ~110 MB | Maven 构建产物，可通过 `mvn clean install` 重新生成 |
| `kc-ui/dist/` | 可变 | 前端构建输出，可通过 `npm run build` 重新生成 |
| `.idea/`, `.vscode/` | 小 | IDE 配置文件 |
| `.env` 文件 | 小 | 敏感信息（数据库密码等） |

### 实际会上传的内容

- ✅ 源代码（Java、Vue、XML、配置文件）
- ✅ SQL 脚本（数据库初始化）
- ✅ 配置文件（pom.xml、package.json、yml 等）
- ✅ 文档和说明

**总大小**: ~1-2 MB（相对较小，完全可以上传）

---

## 上传步骤

### 1. **配置 Git 用户信息**（首次使用）
```powershell
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 2. **添加远程仓库**

#### 方案 A：GitHub
```powershell
# 先在 GitHub 创建新仓库，然后：
cd "E:\快仓\kc"
git remote add origin https://github.com/your-username/kc-attendance.git
git branch -M main
git push -u origin main
```

#### 方案 B：GitLab
```powershell
git remote add origin https://gitlab.com/your-username/kc-attendance.git
git branch -M main
git push -u origin main
```

#### 方案 C：Gitee（码云）
```powershell
git remote add origin https://gitee.com/your-username/kc-attendance.git
git branch -M main
git push -u origin main
```

### 3. **查看当前状态**
```powershell
cd "E:\快仓\kc"
git status
git log --oneline
```

### 4. **后续更新提交**
```powershell
# 修改文件后
git add .
git commit -m "描述你的更改"
git push origin main
```

---

## 关键配置说明

### .gitignore 覆盖的内容

- **Maven**: `**/target/`, `**/*.jar`, `**/*.war`
- **Node.js**: `node_modules/`, `dist/`, `build/`
- **IDE**: `.idea/`, `.vscode/`, `*.iml`
- **敏感文件**: `.env`, `.env.local`
- **系统文件**: `.DS_Store`, `Thumbs.db`

### 环境变量处理

`application-druid.yml` 中包含敏感信息（数据库密码），建议：

1. 提交时使用占位符：
```yaml
username: ${DB_USERNAME:root}
password: ${DB_PASSWORD:123456}
```

2. 本地通过环境变量或 `.env` 文件覆盖（不上传）

3. 或创建 `application-druid.example.yml` 作为模板上传

---

## 完整的提交历史

当前已提交：
```
commit 09e94bb (HEAD -> master)
Author: Your Name <your.email@example.com>
Date:   [日期]

    Initial commit: KC attendance management system
    
    - Backend: Spring Boot 2.5.14 + MyBatis + MySQL
    - Frontend: Vue 2.6.12 + Element UI
    - Version: 3.8.5
```

---

## 常见问题

**Q: 能否恢复 node_modules 和 target？**
A: 可以，运行：
```powershell
npm install        # 恢复前端依赖
mvn clean install  # 恢复后端构建产物
```

**Q: 想要回滚某次提交？**
A: 
```powershell
git reset --hard HEAD~1  # 回退一个提交（谨慎！）
git revert HEAD          # 创建新提交来撤销（安全）
```

**Q: 如何克隆项目？**
A:
```powershell
git clone https://github.com/your-username/kc-attendance.git
cd kc-attendance
npm install
mvn clean install
```

---

## 下一步

1. 在 GitHub/GitLab/Gitee 创建新仓库
2. 按上述步骤添加远程仓库和推送
3. 邀请团队成员协作

