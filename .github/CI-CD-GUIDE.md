# Athena Parent CI/CD 流程优化文档

## 🚀 概述

本文档描述了 athena-parent 项目优化后的 CI/CD 流程，包括持续集成、持续部署、代码质量检查、安全扫描等完整的 DevOps 实践。

## 📋 工作流概览

### 1. 开发分支 CI (`ci-develop.yml`)

**触发条件**: push 到 develop 分支或针对 develop 的 PR

**执行步骤**:

- 🔍 **代码质量检查**: Checkstyle、SpotBugs、SonarQube 缓存
- 🧪 **测试执行**: 单元测试 + 代码覆盖率 (JaCoCo)
- 🔒 **安全扫描**: OWASP 依赖检查 + CodeQL 分析
- 📦 **构建验证**: Maven 打包和构建产物验证
- ⚡ **性能测试**: 基准性能测试（仅 push 触发）
- 📊 **结果汇总**: 工作流执行摘要和通知

**关键特性**:

- 多阶段并行执行，提升效率
- 智能缓存策略减少构建时间
- 全面的测试报告和覆盖率统计
- 自动化安全漏洞检测

### 2. Pull Request CI (`ci-pr.yml`)

**触发条件**: 针对 develop/master 分支的 PR

**执行步骤**:

- 🔄 **变更检测**: 智能识别 Java 代码、文档、配置变更
- ⚡ **快速验证**: 编译检查和基础验证
- 🧪 **分层测试**: 单元测试和集成测试并行执行
- 📊 **质量门禁**: SonarQube 质量门禁检查
- 🔒 **安全检查**: Trivy 漏洞扫描 + OWASP 检查
- 📝 **PR 摘要**: 自动生成检查结果摘要评论

**关键特性**:

- 智能变更检测，只对必要更改执行完整检查
- 并行测试策略提升执行效率
- SonarQube PR 装饰器集成
- 自动化 PR 评论和结果展示

### 3. 主分支 CD (`cd-master.yml`)

**触发条件**: push 到 master 分支或手动触发

**执行步骤**:

- 🔍 **预发布检查**: 版本验证和快速测试
- 🧪 **全面测试**: 多 JDK 版本兼容性测试
- 📊 **质量门禁**: SonarQube 质量门禁等待
- 🔒 **安全验证**: 全面安全扫描
- 📦 **构建发布**: GPG 签名 + Maven Central 发布
- 🏷️ **版本标记**: 自动创建 Git 标签和 GitHub Release
- 🔄 **分支同步**: 自动同步到 develop 分支

**关键特性**:

- 自动版本管理（支持 patch/minor/major）
- GPG 签名确保发布包完整性
- 自动生成变更日志和发布说明
- 完整的发布后验证和分支同步

### 4. 热修复工作流 (`hotfix.yml`)

**触发条件**: push 到 hotfix/** 分支或手动触发

**执行步骤**:

- ✅ **热修复验证**: 版本格式验证和关键测试
- 🔒 **安全检查**: 快速安全漏洞扫描
- 🚀 **紧急发布**: 构建、签名、发布到 Maven Central
- 🔄 **分支合并**: 自动合并回 master 和 develop
- 📢 **通知机制**: 发布通知和执行摘要

**关键特性**:

- 快速发布流程，适合紧急修复
- 自动分支合并和冲突处理
- 冲突时自动创建 PR
- 完整的热修复追踪和通知

### 5. 分支同步 (`branch-sync.yml`)

**触发条件**: 定时执行（每日 8:00 UTC）或手动触发

**执行步骤**:

- 📊 **同步检查**: 检测分支间的提交差异
- 🔄 **自动合并**: 尝试自动合并分支
- ⚠️ **冲突处理**: 冲突时创建同步 PR
- 🔧 **强制同步**: 支持强制同步选项
- ✅ **验证测试**: 同步后的项目结构验证

**关键特性**:

- 定时自动化分支同步
- 智能冲突检测和处理
- 支持手动干预和强制同步
- 失败时自动创建问题追踪

## 🛠️ Maven 插件集成

### 代码质量插件

- **Checkstyle**: Google 代码规范检查
- **SpotBugs**: 静态代码分析和 Bug 检测
- **JaCoCo**: 代码覆盖率统计（最低 70%）
- **SonarQube**: 综合代码质量分析

### 测试插件

- **Surefire**: 单元测试执行
- **Failsafe**: 集成测试执行
- **性能测试**: 支持 JMeter 和自定义性能测试

### 安全插件

- **OWASP Dependency Check**: 依赖漏洞扫描
- **Trivy**: 容器和文件系统漏洞扫描

## 🔧 配置要求

### GitHub Secrets

```
# Maven Central 发布
OSSRH_USERNAME=你的OSSRH用户名
OSSRH_TOKEN=你的OSSRH令牌

# GPG 签名
GPG_PRIVATE_KEY=你的GPG私钥
GPG_PASSPHRASE=你的GPG密码
GPG_KEY_ID=你的GPG密钥ID

# 代码质量
SONAR_TOKEN=你的SonarCloud令牌
CODECOV_TOKEN=你的Codecov令牌

# 分支操作
PAT_TOKEN=具有写权限的Personal Access Token
```

### SonarCloud 配置

1. 在 SonarCloud 中创建项目
2. 配置项目 Key: `athena-parent`
3. 配置组织: `gls-athena`
4. 设置质量门禁规则

## 📊 监控和报告

### 自动化报告

- **测试报告**: JUnit 格式，集成到 GitHub Checks
- **覆盖率报告**: JaCoCo + Codecov 可视化
- **安全报告**: OWASP HTML 报告上传到 Artifacts
- **质量报告**: SonarQube Dashboard

### 通知机制

- **PR 评论**: 自动生成检查结果摘要
- **工作流摘要**: GitHub Actions Summary 展示
- **失败通知**: 自动创建 Issue 追踪问题

## 🚀 使用指南

### 日常开发

1. 在 `develop` 分支进行开发
2. 创建 PR 时会自动触发 CI 检查
3. 合并到 `develop` 后会执行完整 CI 流程

### 版本发布

1. 合并 `develop` 到 `master`
2. 自动触发 CD 流程
3. 或使用 workflow_dispatch 手动指定版本类型

### 热修复发布

1. 从 `master` 创建 `hotfix/x.y.z` 分支
2. 完成修复后推送
3. 自动执行热修复发布流程

### 强制同步

```bash
# 手动触发分支同步
gh workflow run branch-sync.yml \
  -f source_branch=master \
  -f target_branch=develop \
  -f force_sync=true
```

## 🎯 最佳实践

1. **提交规范**: 使用 Conventional Commits 格式
2. **分支策略**: GitFlow 工作流
3. **测试驱动**: 保持高代码覆盖率
4. **安全优先**: 定期更新依赖版本
5. **文档同步**: 保持文档与代码同步更新

## 🔍 故障排除

### 常见问题

1. **GPG 签名失败**: 检查 GPG 密钥配置
2. **Maven Central 发布失败**: 验证 OSSRH 凭据
3. **SonarQube 分析失败**: 检查令牌和项目配置
4. **分支同步冲突**: 使用手动 PR 解决

### 调试方法

1. 查看 GitHub Actions 日志
2. 使用 `act` 工具本地调试
3. 检查 Maven 详细输出
4. 验证 Secrets 配置

---

通过这套完整的 CI/CD 流程，athena-parent 项目将具备：

- 🚀 快速可靠的构建和部署
- 🔒 全面的安全和质量保障
- 📊 完整的监控和报告
- 🔄 自动化的分支管理
- 📦 规范的版本发布流程
