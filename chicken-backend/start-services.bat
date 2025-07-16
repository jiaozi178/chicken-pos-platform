@echo off
echo ========================================
echo   Starting Chicken POS System
echo ========================================

echo starting Redis...
:: 启动 Redis 服务器（后台运行）
start "Redis Server" redis-server

:: 等待 Redis 启动
timeout /t 3 /nobreak >nul

echo Redis started
echo starting backend project...

:: 确保在 chicken-backend 目录下执行
cd /d "%~dp0"

:: 使用 Maven 的 -D 参数设置工作目录
mvn -f server/pom.xml spring-boot:run -Dspring-boot.run.workingDirectory="%cd%"
