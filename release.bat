@echo off
setlocal enabledelayedexpansion

REM 自动版本升级和发布脚本 (Windows版本)
REM 用法: release.bat [版本号]
REM 示例: release.bat 1.0.0

if "%~1"=="" (
    echo [ERROR] 请提供版本号，例如: release.bat 1.0.0
    exit /b 1
)

set NEW_VERSION=%~1

REM 验证版本格式 (简单检查)
echo %NEW_VERSION% | findstr /R "^[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*$" >nul
if errorlevel 1 (
    echo [ERROR] 版本格式不正确，应为 x.y.z 格式
    exit /b 1
)

echo [INFO] 开始自动版本升级流程...

REM 检查必要工具
where git >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Git命令不存在，请先安装Git
    exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven命令不存在，请先安装Maven
    exit /b 1
)

echo [INFO] 所有必要工具已安装

REM 检查Git状态
git diff --quiet
if errorlevel 1 (
    echo [ERROR] 存在未提交的更改，请先提交或暂存
    exit /b 1
)

git diff --cached --quiet
if errorlevel 1 (
    echo [ERROR] 存在未提交的暂存更改，请先提交
    exit /b 1
)

echo [INFO] Git状态检查通过

REM 获取当前版本
for /f "tokens=*" %%i in ('findstr /R "<revision>.*</revision>" pom.xml') do set CURRENT_LINE=%%i
for /f "tokens=2 delims=<>" %%a in ("%CURRENT_LINE%") do set CURRENT_VERSION=%%a

echo [INFO] 当前版本: %CURRENT_VERSION%
echo [INFO] 目标版本: %NEW_VERSION%

echo.
echo [WARNING] 即将执行以下操作:
echo 1. 将版本从 %CURRENT_VERSION% 升级到 %NEW_VERSION%
echo 2. 编译和测试项目
echo 3. 提交更改到develop分支
echo 4. 合并develop到master分支
echo 5. 在master分支创建标签 v%NEW_VERSION%
echo 6. 推送所有更改和标签
echo 7. 将develop分支版本更新为下个SNAPSHOT版本
echo.

set /p CONFIRM="确认继续? (y/N): "
if /i not "%CONFIRM%"=="y" (
    echo [INFO] 操作已取消
    exit /b 0
)

REM 获取当前分支
for /f "tokens=*" %%i in ('git branch --show-current') do set CURRENT_BRANCH=%%i
echo [INFO] 当前分支: %CURRENT_BRANCH%

REM 确保在develop分支
if not "%CURRENT_BRANCH%"=="develop" (
    echo [INFO] 切换到develop分支...
    git checkout develop
    if errorlevel 1 (
        echo [ERROR] 切换到develop分支失败
        exit /b 1
    )
    git pull origin develop
) else (
    echo [INFO] 拉取最新的develop分支...
    git pull origin develop
)

REM 更新版本号
echo [INFO] 更新版本号到 %NEW_VERSION%...
call mvn versions:set -DnewVersion=%NEW_VERSION% -DgenerateBackupPoms=false
if errorlevel 1 (
    echo [ERROR] 版本号更新失败
    exit /b 1
)
echo [SUCCESS] 版本号更新成功

REM 编译和测试
echo [INFO] 编译和测试项目...
call mvn clean compile test
if errorlevel 1 (
    echo [ERROR] 编译或测试失败
    exit /b 1
)
echo [SUCCESS] 编译和测试通过

REM 提交版本更改
echo [INFO] 提交版本更改...
git add .
git commit -m "chore: bump version to %NEW_VERSION%"
git push origin develop
if errorlevel 1 (
    echo [ERROR] 推送develop分支失败
    exit /b 1
)

REM 切换到master分支
echo [INFO] 切换到master分支...
git checkout master
if errorlevel 1 (
    echo [ERROR] 切换到master分支失败
    exit /b 1
)
git pull origin master

REM 合并develop分支到master
echo [INFO] 合并develop分支到master...
git merge develop --no-ff -m "release: merge develop for version %NEW_VERSION%"
if errorlevel 1 (
    echo [ERROR] 合并develop到master失败
    exit /b 1
)

REM 创建标签
echo [INFO] 创建标签 v%NEW_VERSION%...
git tag -a "v%NEW_VERSION%" -m "Release version %NEW_VERSION%"
if errorlevel 1 (
    echo [ERROR] 创建标签失败
    exit /b 1
)

REM 推送master分支和标签
echo [INFO] 推送master分支和标签...
git push origin master
if errorlevel 1 (
    echo [ERROR] 推送master分支失败
    exit /b 1
)

git push origin "v%NEW_VERSION%"
if errorlevel 1 (
    echo [ERROR] 推送标签失败
    exit /b 1
)

REM 切换回develop分支
echo [INFO] 切换回develop分支...
git checkout develop
if errorlevel 1 (
    echo [ERROR] 切换回develop分支失败
    exit /b 1
)

REM 计算下个SNAPSHOT版本
for /f "tokens=1,2,3 delims=." %%a in ("%NEW_VERSION%") do (
    set MAJOR=%%a
    set MINOR=%%b
    set PATCH=%%c
)

set /a NEXT_PATCH=%PATCH%+1
set NEXT_VERSION=%MAJOR%.%MINOR%.%NEXT_PATCH%-SNAPSHOT

echo [INFO] 更新develop分支版本为 %NEXT_VERSION%...
call mvn versions:set -DnewVersion=%NEXT_VERSION% -DgenerateBackupPoms=false
if errorlevel 1 (
    echo [ERROR] 更新SNAPSHOT版本失败
    exit /b 1
)

git add .
git commit -m "chore: bump version to %NEXT_VERSION%"
git push origin develop
if errorlevel 1 (
    echo [ERROR] 推送SNAPSHOT版本失败
    exit /b 1
)

echo [SUCCESS] 版本 %NEW_VERSION% 发布完成!
echo [INFO] 标签: v%NEW_VERSION%
echo [INFO] 下次开发版本: %NEXT_VERSION%

pause
