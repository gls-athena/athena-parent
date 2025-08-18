# Athena Parent CI/CD 配置说明

本项目基于 GitHub Actions 和 Maven 实现了完整的 CI/CD 流程，支持自动版本管理、测试、构建和发布。

## 🏗️ 工作流概览

### 1. 开发分支 CI (`ci-develop.yml`)

**触发条件**: 推送到 `develop` 分支或向 `develop` 分支提交 PR

**功能**:

- 自动运行单元测试和集成测试
- 生成测试覆盖率报告
- 安全漏洞扫描 (OWASP)
- 代码质量分析 (SonarQube，可选)
- 构建验证

### 2. 主分支发布 CD (`cd-master.yml`)

**触发条件**:

- 推送到 `master` 分支
- 手动触发 (支持选择版本升级类型)

**功能**:

- 自动计算下一个版本号
- 运行完整测试套件
- 创建发布版本标签
- 生成 GitHub Release
- 部署到 Maven 仓库
- 自动更新到下一个开发版本

**版本升级类型**:

- `patch`: 修订版本号 (1.0.0 → 1.0.1)
- `minor`: 次版本号 (1.0.0 → 1.1.0)
- `major`: 主版本号 (1.0.0 → 2.0.0)

### 3. Pull Request CI (`ci-pr.yml`)

**触发条件**: 向 `develop` 或 `master` 分支提交 PR

**功能**:

- 代码验证和编译检查
- 运行单元测试和集成测试
- 代码风格检查 (Checkstyle)
- 静态代码分析 (SpotBugs)
- 安全检查
- 在 PR 中自动评论测试结果

### 4. 热修复发布 (`hotfix.yml`)

**触发条件**: 手动触发

**功能**:

- 基于指定分支创建热修复分支
- 快速版本发布
- 创建热修复标签和 Release
- 自动创建合并回原分支的 PR

### 5. 分支同步 (`branch-sync.yml`)

**触发条件**:

- 手动触发
- 定时任务 (每周日凌晨 2 点)

**功能**:

- 自动同步 `develop` 到 `master`
- 支持反向同步 `master` 到 `develop`
- 智能检测是否有新提交需要同步

## 🔧 配置要求

### GitHub Secrets 配置

需要在 GitHub 仓库设置中配置以下 Secrets：

```
GITHUB_TOKEN          # GitHub 自动提供，无需手动设置
MAVEN_USERNAME         # Maven 仓库用户名 (可选)
MAVEN_PASSWORD         # Maven 仓库密码 (可选)  
SONAR_TOKEN           # SonarQube 令牌 (可选)
GPG_PASSPHRASE        # GPG 签名密码 (可选)
```

### Maven 配置

项目已配置以下插件支持 CI/CD：

1. **Flatten Maven Plugin**: 支持 `${revision}` 占位符版本
2. **Maven Release Plugin**: 自动版本管理和标签创建
3. **Maven SCM Plugin**: Git 集成支持

## 📋 使用指南

### 日常开发流程

1. **功能开发**:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/new-feature
   # 开发功能...
   git push origin feature/new-feature
   # 创建 PR 到 develop 分支
   ```

2. **代码提交到 develop**:
    - 自动触发 CI 流程
    - 运行测试和代码质量检查
    - 生成测试报告

3. **发布准备**:
   ```bash
   git checkout master
   git merge develop
   git push origin master
   ```
    - 自动触发发布流程
    - 创建新版本标签
    - 发布到 Maven 仓库

### 手动发布流程

1. **进入 Actions 页面** → **CD - Master Branch Release**
2. **点击 "Run workflow"**
3. **选择版本升级类型**:
    - `patch`: 修复版本 (推荐用于 bug 修复)
    - `minor`: 功能版本 (推荐用于新功能)
    - `major`: 主要版本 (推荐用于重大变更)

### 热修复流程

1. **进入 Actions 页面** → **Hotfix Release**
2. **填写热修复信息**:
    - `hotfix_version`: 热修复版本号 (如 1.0.1)
    - `base_branch`: 基础分支 (通常是 master)
    - `description`: 热修复描述
3. **执行后自动**:
    - 创建热修复分支
    - 发布热修复版本
    - 创建合并回原分支的 PR

## 🔍 版本管理策略

### 版本号规则

采用语义化版本 (Semantic Versioning):

- **主版本号**: 不兼容的 API 修改
- **次版本号**: 向下兼容的功能性新增
- **修订版本号**: 向下兼容的问题修正

### 分支策略

- **`develop`**: 开发分支，用于功能开发和集成
- **`master`**: 主分支，用于生产发布
- **`feature/*`**: 功能分支，从 develop 创建
- **`hotfix/*`**: 热修复分支，从 master 创建

## 📊 监控和报告

### 自动生成的报告

- **测试报告**: 单元测试和集成测试结果
- **覆盖率报告**: 代码覆盖率统计
- **安全报告**: 依赖漏洞扫描结果
- **质量报告**: 代码质量分析 (如果配置了 SonarQube)

### 通知机制

- **GitHub PR 评论**: 自动在 PR 中评论测试结果
- **GitHub Release**: 自动创建发布说明
- **工作流状态**: 通过 GitHub Actions 界面查看

## 🛠️ 故障排除

### 常见问题

1. **版本号冲突**:
    - 确保 `pom.xml` 中使用 `${revision}` 占位符
    - 检查所有子模块版本配置

2. **Maven 部署失败**:
    - 验证 `MAVEN_USERNAME` 和 `MAVEN_PASSWORD` 配置
    - 检查仓库访问权限

3. **测试失败**:
    - 检查测试代码和依赖
    - 确保测试环境配置正确

4. **Git 推送失败**:
    - 确保 GitHub Token 有足够权限
    - 检查分支保护规则设置

### 调试技巧

1. **查看工作流日志**: GitHub Actions → 具体工作流 → 查看详细日志
2. **本地验证**: 在本地运行相同的 Maven 命令
3. **分步调试**: 注释部分步骤，逐步排查问题

## 📝 自定义配置

### 添加新的检查步骤

在相应的 `.yml` 文件中添加新的 `step`：

```yaml
- name: Custom Check
  run: |
    # 自定义检查命令
    mvn custom:check
```

### 修改版本策略

在 `cd-master.yml` 中修改版本计算逻辑：

```yaml
# 自定义版本递增逻辑
- name: Calculate next version
  run: |
    # 自定义版本计算
```

### 添加部署目标

在部署步骤中添加新的部署目标：

```yaml
- name: Deploy to Custom Registry
  run: |
    mvn deploy -DaltDeploymentRepository=custom::default::https://custom-repo.com
```

---

## 🤝 贡献指南

1. 在修改 CI/CD 配置前，请先在测试分支验证
2. 重要变更请创建 PR 并邀请团队 Review
3. 保持工作流文件的注释和文档更新
4. 遵循 GitHub Actions 的最佳实践

## 📞 支持

如有问题或建议，请：

1. 查看本文档的故障排除部分
2. 在项目中创建 Issue
3. 联系 DevOps 团队
