#!/bin/bash
# KC考勤系统 - 后端启动脚本

# 固定端口配置
BACKEND_PORT=8080
JAR_NAME="kc-admin.jar"
JAR_PATH="kc-master/kc-admin/target/${JAR_NAME}"
LOG_FILE="/tmp/kc-backend.log"

echo "========================================="
echo "  KC考勤系统 - 后端启动脚本"
echo "========================================="
echo ""

# 检查Java
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java，请先安装JDK 1.8+"
    echo "安装命令: sudo yum install java-1.8.0-openjdk-devel -y"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven，请先安装Maven 3.6+"
    echo "安装命令: sudo yum install maven -y"
    exit 1
fi

# 检查MySQL
if ! systemctl is-active --quiet mysqld && ! systemctl is-active --quiet mysql; then
    echo "⚠️  警告: MySQL服务未运行"
    echo "启动命令: sudo systemctl start mysqld"
fi

# 检查Redis
if ! redis-cli ping &> /dev/null; then
    echo "⚠️  警告: Redis服务未运行或未安装"
    echo "启动命令: sudo systemctl start redis"
fi

echo ""
echo "✅ Java版本: $(java -version 2>&1 | head -n 1)"
echo "✅ Maven版本: $(mvn -version | head -n 1)"
echo ""

# 进入项目目录
cd "$(dirname "$0")" || exit 1

# 检查端口占用并关闭旧进程
echo "🔍 检查端口 ${BACKEND_PORT} 占用情况..."
if lsof -ti:${BACKEND_PORT} > /dev/null 2>&1; then
    echo "⚠️  端口 ${BACKEND_PORT} 已被占用"

    # 检查是否是 kc-admin 进程
    OLD_PID=$(ps aux | grep "${JAR_NAME}" | grep -v grep | awk '{print $2}')
    if [ ! -z "$OLD_PID" ]; then
        echo "🛑 发现旧的后端进程 (PID: ${OLD_PID})，正在关闭..."
        kill -15 ${OLD_PID}
        sleep 3

        # 如果进程还在运行，强制kill
        if ps -p ${OLD_PID} > /dev/null 2>&1; then
            echo "⚠️  进程未正常关闭，强制终止..."
            kill -9 ${OLD_PID}
            sleep 2
        fi
        echo "✅ 旧进程已关闭"
    else
        # 端口被其他进程占用
        PORT_PID=$(lsof -ti:${BACKEND_PORT})
        echo "❌ 端口被其他进程占用 (PID: ${PORT_PID})"
        echo "请手动处理该进程或更改端口配置"
        exit 1
    fi
else
    echo "✅ 端口 ${BACKEND_PORT} 可用"
fi

echo ""
echo "📦 正在编译项目..."
cd kc-master
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败，请检查错误信息"
    exit 1
fi

echo ""
echo "🚀 正在启动后端服务..."
echo "端口: ${BACKEND_PORT}"
echo "日志: ${LOG_FILE}"
echo "访问地址: http://localhost:${BACKEND_PORT}"
echo "API文档: http://localhost:${BACKEND_PORT}/swagger-ui.html"
echo ""

# 后台启动
cd kc-admin/target
nohup java -jar ${JAR_NAME} --server.port=${BACKEND_PORT} > ${LOG_FILE} 2>&1 &
NEW_PID=$!

echo "✅ 后端服务已启动 (PID: ${NEW_PID})"
echo ""
echo "查看日志: tail -f ${LOG_FILE}"
echo "停止服务: kill ${NEW_PID}"
echo ""

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 检查进程是否还在运行
if ps -p ${NEW_PID} > /dev/null 2>&1; then
    echo "✅ 后端服务运行正常"
    tail -20 ${LOG_FILE}
else
    echo "❌ 后端服务启动失败，请查看日志: ${LOG_FILE}"
    tail -50 ${LOG_FILE}
    exit 1
fi
