# Athena Parent CI/CD 流程说明

本文档描述了 athena-parent 项目的持续集成和持续部署（CI/CD）流程。

## 工作流概览

### 1. CI 流程 (ci.yml)

**触发条件：**

- 推送到 `develop` 分支
- 针对 `develop` 或 `master` 分支的 Pull Request

**包含的作业：**

- **test**: 运行单元测试和生成测试报告
- **code-quality**: 使用 SonarCloud 进行代码质量分析
- **build**: 编译代码并打包构件

### 2. CD 流程 (cd.yml)

**触发条件：**

- 推送到 `master` 分支
- 发布新版本

**功能：**

- 自动部署到 Maven Central
- 创建 Git 标签
- 生成部署摘要

### 3. 发布流程 (release.yml)

**触发条件：**

- 手动触发（workflow_dispatch）

**功能：**

- 管理版本号
- 创建发布标签
- 合并 develop 到 master
- 创建 GitHub Release

### 4. 安全扫描 (security.yml)

**触发条件：**

- 每周一凌晨 2 点自动运行
- 手动触发
- 推送到主要分支

**功能：**

- OWASP 依赖检查
- Snyk 安全扫描

### 5. 代码风格检查 (code-style.yml)

**触发条件：**

- Pull Request 到主要分支

**功能：**

- Checkstyle 检查
- SpotBugs 静态分析

## 分支策略

### Develop 分支

- 主要开发分支
- 所有新功能和 bug 修复都先合并到此分支
- 触发 CI 流程进行测试和质量检查

### Master 分支

- 生产就绪的代码
- 只接受来自 develop 分支的合并
- 触发 CD 流程自动部署到 Maven Central

## 必需的 GitHub Secrets

为了使 CI/CD 流程正常工作，需要在 GitHub 仓库设置中配置以下 Secrets：

### 发布相关

- `GPG_PRIVATE_KEY`: GPG 私钥（用于签名）
- `GPG_PASSPHRASE`: GPG 密码短语
- `MAVEN_CENTRAL_USERNAME`: Maven Central 用户名
- `MAVEN_CENTRAL_PASSWORD`: Maven Central 密码
- `RELEASE_TOKEN`: 具有写入权限的 GitHub Token

### 代码质量

- `SONAR_TOKEN`: SonarCloud 访问令牌
- `SNYK_TOKEN`: Snyk 访问令牌

## 版本管理

项目使用语义化版本控制（SemVer）：

- **主版本号**: 不兼容的 API 更改
- **次版本号**: 向后兼容的功能新增
- **修订号**: 向后兼容的 bug 修复

开发版本使用 `-SNAPSHOT` 后缀。

## 发布流程

### 自动发布（推荐）

1. 在 GitHub Actions 中手动触发 "Release" 工作流
2. 输入发布版本号（如 `1.0.0`）
3. 输入下一个开发版本号（如 `1.0.1-SNAPSHOT`）
4. 工作流将自动：
    - 更新版本号
    - 创建标签
    - 合并到 master
    - 触发部署
    - 准备下一个开发迭代

### 手动发布

如果需要手动控制发布过程：

1. 在 develop 分支更新版本号
2. 合并到 master 分支
3. 推送到远程仓库触发 CD 流程

## 代码质量要求

- **测试覆盖率**: 建议达到 80% 以上
- **SonarCloud 质量门**: 必须通过
- **安全扫描**: 高危漏洞必须修复
- **代码风格**: 必须符合 Checkstyle 规范

## 故障排除

### 常见问题

1. **GPG 签名失败**
    - 检查 GPG_PRIVATE_KEY 和 GPG_PASSPHRASE 是否正确配置
    - 确保 GPG 密钥未过期

2. **Maven Central 部署失败**
    - 验证 MAVEN_CENTRAL_USERNAME 和 MAVEN_CENTRAL_PASSWORD
    - 检查 pom.xml 中的发布配置

3. **SonarCloud 分析失败**
    - 确认 SONAR_TOKEN 有效
    - 检查 SonarCloud 项目配置

4. **测试失败**
    - 查看测试报告了解具体失败原因
    - 确保所有依赖正确安装

## 自动化特性

### Dependabot

- 自动检查依赖更新
- 每周一创建 PR 更新依赖
- 忽略 Spring Boot 主版本更新（需手动审查）

### 安全扫描

- 定期检查已知漏洞
- 自动生成安全报告
- 集成到 GitHub Security 选项卡

## 监控和通知

- 构建状态显示在 README 徽章中
- 失败的构建会发送邮件通知（如已配置）
- 部署状态可在 GitHub Actions 选项卡查看

## 贡献指南

1. 从 develop 分支创建功能分支
2. 开发完成后创建 Pull Request 到 develop
3. 确保所有检查通过后合并
4. 定期从 develop 发布到 master
