#!/bin/bash
# KC考勤系统 - 前端启动脚本

# 固定端口配置
FRONTEND_PORT=80
LOG_FILE="/tmp/kc-frontend.log"

echo "========================================="
echo "  KC考勤系统 - 前端启动脚本"
echo "========================================="
echo ""

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo "❌ 错误: 未找到Node.js，请先安装Node.js 12+"
    echo "下载地址: https://nodejs.org/"
    exit 1
fi

# 检查npm
if ! command -v npm &> /dev/null; then
    echo "❌ 错误: 未找到npm"
    exit 1
fi

echo "✅ Node.js版本: $(node -v)"
echo "✅ npm版本: $(npm -v)"
echo ""

# 进入前端目录
cd "$(dirname "$0")/kc-ui" || exit 1

# 检查端口占用并关闭旧进程
echo "🔍 检查端口 ${FRONTEND_PORT} 占用情况..."

# 查找占用该端口的 vue-cli-service 进程
OLD_PID=$(ps aux | grep "vue-cli-service.*--port ${FRONTEND_PORT}" | grep -v grep | awk '{print $2}')

if [ ! -z "$OLD_PID" ]; then
    echo "⚠️  端口 ${FRONTEND_PORT} 已被旧的前端服务占用 (PID: ${OLD_PID})"
    echo "🛑 正在关闭旧进程..."

    # 关闭旧进程
    pkill -f "vue-cli-service.*--port ${FRONTEND_PORT}"
    sleep 3

    # 检查是否还在运行
    if ps -p ${OLD_PID} > /dev/null 2>&1; then
        echo "⚠️  进程未正常关闭，强制终止..."
        kill -9 ${OLD_PID}
        sleep 2
    fi
    echo "✅ 旧进程已关闭"
elif lsof -ti:${FRONTEND_PORT} > /dev/null 2>&1; then
    # 端口被其他进程占用
    PORT_PID=$(lsof -ti:${FRONTEND_PORT})
    PROCESS_NAME=$(ps -p ${PORT_PID} -o comm=)
    echo "⚠️  端口 ${FRONTEND_PORT} 被其他进程占用"
    echo "进程: ${PROCESS_NAME} (PID: ${PORT_PID})"

    # 如果是nginx，提示用户
    if [[ "$PROCESS_NAME" == *"nginx"* ]]; then
        echo "❌ 端口被Nginx占用，前端将使用其他可用端口"
        echo "   建议: 配置Nginx反向代理到前端开发服务器"
        # 不退出，让vue-cli自动找其他端口
    else
        echo "请手动处理该进程或更改端口配置"
        exit 1
    fi
else
    echo "✅ 端口 ${FRONTEND_PORT} 可用"
fi

# 检查是否已安装依赖
if [ ! -d "node_modules" ]; then
    echo "📦 首次启动，正在安装依赖..."
    echo "   这可能需要几分钟时间，请耐心等待..."
    echo ""
    npm install --registry=https://registry.npmmirror.com

    if [ $? -ne 0 ]; then
        echo "❌ 依赖安装失败，请检查网络连接"
        exit 1
    fi
fi

echo ""
echo "🚀 正在启动前端开发服务器..."
echo "端口: ${FRONTEND_PORT}"
echo "日志: ${LOG_FILE}"
echo "访问地址: http://localhost:${FRONTEND_PORT}"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

# 启动开发服务器（后台运行）
nohup npm exec cross-env NODE_OPTIONS=--openssl-legacy-provider vue-cli-service serve --port ${FRONTEND_PORT} > ${LOG_FILE} 2>&1 &
NEW_PID=$!

echo "✅ 前端服务已启动 (PID: ${NEW_PID})"
echo ""
echo "查看日志: tail -f ${LOG_FILE}"
echo "停止服务: kill ${NEW_PID}"
echo ""

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 15

# 检查进程是否还在运行
if ps -p ${NEW_PID} > /dev/null 2>&1; then
    echo "✅ 前端服务运行正常"
    echo ""
    tail -10 ${LOG_FILE} | grep -E "App running|Local|Network" || echo "服务正在启动中..."
else
    echo "❌ 前端服务启动失败，请查看日志: ${LOG_FILE}"
    tail -30 ${LOG_FILE}
    exit 1
fi
