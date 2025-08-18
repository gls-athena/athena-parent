#!/bin/bash

# 自动版本升级和发布脚本
# 用法: ./release.sh [版本号]
# 示例: ./release.sh 1.0.0

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的信息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        print_error "$1 命令不存在，请先安装"
        exit 1
    fi
}

# 检查必要的工具
check_prerequisites() {
    print_info "检查必要工具..."
    check_command git
    check_command mvn
    print_success "所有必要工具已安装"
}

# 检查Git状态
check_git_status() {
    print_info "检查Git状态..."

    # 检查是否有未提交的更改
    if ! git diff --quiet; then
        print_error "存在未提交的更改，请先提交或暂存"
        exit 1
    fi

    if ! git diff --cached --quiet; then
        print_error "存在未提交的暂存更改，请先提交"
        exit 1
    fi

    print_success "Git状态检查通过"
}

# 获取当前版本
get_current_version() {
    local version=$(grep -o '<revision>[^<]*</revision>' pom.xml | sed 's/<revision>\(.*\)<\/revision>/\1/')
    echo $version
}

# 验证版本格式
validate_version() {
    local version=$1
    if [[ ! $version =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        print_error "版本格式不正确，应为 x.y.z 格式"
        exit 1
    fi
}

# 更新版本号
update_version() {
    local new_version=$1
    print_info "更新版本号到 $new_version..."

    # 使用Maven versions插件更新版本
    mvn versions:set -DnewVersion=$new_version -DgenerateBackupPoms=false

    if [ $? -eq 0 ]; then
        print_success "版本号更新成功"
    else
        print_error "版本号更新失败"
        exit 1
    fi
}

# 编译和测试
build_and_test() {
    print_info "编译和测试项目..."

    mvn clean compile test

    if [ $? -eq 0 ]; then
        print_success "编译和测试通过"
    else
        print_error "编译或测试失败"
        exit 1
    fi
}

# Git操作
git_operations() {
    local version=$1
    local current_branch=$(git branch --show-current)

    print_info "当前分支: $current_branch"

    # 确保在develop分支
    if [ "$current_branch" != "develop" ]; then
        print_info "切换到develop分支..."
        git checkout develop
        git pull origin develop
    else
        print_info "拉取最新的develop分支..."
        git pull origin develop
    fi

    # 提交版本更改
    print_info "提交版本更改..."
    git add .
    git commit -m "chore: bump version to $version"
    git push origin develop

    # 切换到master分支
    print_info "切换到master分支..."
    git checkout master
    git pull origin master

    # 合并develop分支到master
    print_info "合并develop分支到master..."
    git merge develop --no-ff -m "release: merge develop for version $version"

    # 创建标签
    print_info "创建标签 v$version..."
    git tag -a "v$version" -m "Release version $version"

    # 推送master分支和标签
    print_info "推送master分支和标签..."
    git push origin master
    git push origin "v$version"

    # 切换回develop分支并更新下个开发版本
    print_info "切换回develop分支..."
    git checkout develop

    # 计算下个SNAPSHOT版本
    IFS='.' read -ra VERSION_PARTS <<< "$version"
    MAJOR=${VERSION_PARTS[0]}
    MINOR=${VERSION_PARTS[1]}
    PATCH=${VERSION_PARTS[2]}

    # 增加PATCH版本号
    NEXT_PATCH=$((PATCH + 1))
    NEXT_VERSION="$MAJOR.$MINOR.$NEXT_PATCH-SNAPSHOT"

    print_info "更新develop分支版本为 $NEXT_VERSION..."
    mvn versions:set -DnewVersion=$NEXT_VERSION -DgenerateBackupPoms=false

    git add .
    git commit -m "chore: bump version to $NEXT_VERSION"
    git push origin develop

    print_success "Git操作完成"
}

# 主函数
main() {
    print_info "开始自动版本升级流程..."

    # 检查参数
    if [ $# -eq 0 ]; then
        print_error "请提供版本号，例如: ./release.sh 1.0.0"
        exit 1
    fi

    local new_version=$1

    # 验证版本格式
    validate_version $new_version

    # 检查先决条件
    check_prerequisites

    # 检查Git状态
    check_git_status

    # 显示当前版本
    local current_version=$(get_current_version)
    print_info "当前版本: $current_version"
    print_info "目标版本: $new_version"

    # 确认操作
    echo
    print_warning "即将执行以下操作:"
    echo "1. 将版本从 $current_version 升级到 $new_version"
    echo "2. 编译和测试项目"
    echo "3. 提交更改到develop分支"
    echo "4. 合并develop到master分支"
    echo "5. 在master分支创建标签 v$new_version"
    echo "6. 推送所有更改和标签"
    echo "7. 将develop分支版本更新为下个SNAPSHOT版本"
    echo
    read -p "确认继续? (y/N): " -n 1 -r
    echo

    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "操作已取消"
        exit 0
    fi

    # 更新版本号
    update_version $new_version

    # 编译和测试
    build_and_test

    # Git操作
    git_operations $new_version

    print_success "版本 $new_version 发布完成!"
    print_info "标签: v$new_version"
    print_info "下次开发版本: $(get_current_version)"
}

# 执行主函数
main "$@"
