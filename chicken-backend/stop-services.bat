@echo off
echo ===== Stopping Development Environment Services =====

echo Stopping Redis service...
taskkill /f /im redis-server.exe 2>nul
if %errorlevel% equ 0 (
    echo Redis has been stopped
) else (
    echo No running Redis service found
)

echo ===== All Services Have Been Stopped =====