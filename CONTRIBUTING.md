# 贡献指南

感谢您对 Athena Parent 项目的关注和贡献！本文档将帮助您了解如何为项目做出贡献。

## 📋 目录

- [开发环境要求](#开发环境要求)
- [获取源码](#获取源码)
- [项目结构](#项目结构)
- [开发规范](#开发规范)
- [代码提交](#代码提交)
- [Pull Request](#pull-request)
- [问题反馈](#问题反馈)
- [社区支持](#社区支持)

## 🛠️ 开发环境要求

在开始贡献之前，请确保您的开发环境满足以下要求：

### 必需环境

- **Java**: 21 或更高版本
- **Maven**: 3.8.0 或更高版本
- **Git**: 2.20 或更高版本

### 推荐工具

- **IDE**: IntelliJ IDEA 2023.3+ 或 Eclipse 2023-12+
- **Maven Helper**: 用于依赖管理的IDE插件
- **Lombok**: 用于简化代码的注解处理器
- **MapStruct**: 用于对象映射的代码生成器

### 环境验证

```bash
java --version
mvn --version
git --version
```

## 📥 获取源码

### 1. Fork 仓库

在 GitHub 上 Fork 本仓库到您的个人账户。

### 2. 克隆代码

```bash
git clone https://github.com/your-username/athena.git
cd athena/athena-parent
```

### 3. 添加上游仓库

```bash
git remote add upstream https://github.com/glseven/athena.git
```

### 4. 构建项目

```bash
mvn clean install
```

## 🏗️ 项目结构

```
athena-parent/
├── athena-bom/                    # 依赖版本管理（BOM）
└── athena-project/               # 核心项目模块
    ├── athena-cloud/             # 云原生支持
    ├── athena-common/            # 通用工具包
    ├── athena-sdk/              # 第三方SDK集成
    └── athena-starter/          # 自动配置启动器
```

### 模块说明

- **athena-bom**: 统一管理所有依赖版本
- **athena-cloud**: 提供微服务基础能力
- **athena-common**: 通用工具类和公共组件
- **athena-sdk**: 第三方服务SDK封装
- **athena-starter**: Spring Boot 自动配置启动器

## 📝 开发规范

### 代码风格

我们遵循 Google Java Style Guide，请确保您的代码符合以下要求：

#### 1. 命名规范

- **类名**: 使用 PascalCase（如：`UserService`）
- **方法名**: 使用 camelCase（如：`getUserById`）
- **常量**: 使用 UPPER_SNAKE_CASE（如：`MAX_RETRY_COUNT`）
- **包名**: 使用小写字母，以点分隔（如：`com.gls.athena.common`）

#### 2. 注释规范

```java
/**
 * 用户服务接口
 *
 * @author Your Name
 * @since 0.0.1
 */
public interface UserService {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID，不能为空
     * @return 用户信息，如果不存在则返回null
     * @throws IllegalArgumentException 当userId为空时抛出
     */
    User getUserById(Long userId);
}
```

#### 3. 代码格式

- 使用 4 个空格缩进，不使用 Tab
- 每行代码长度不超过 120 个字符
- 方法之间保留一个空行
- import 语句按字母顺序排列

### 依赖管理

- 新增依赖必须在 `athena-bom` 模块中统一管理版本
- 优先使用已有的依赖，避免引入功能重复的库
- 新增依赖需要在 PR 中说明引入原因

### 测试要求

- 所有新功能必须编写单元测试
- 测试覆盖率不低于 80%
- 测试方法命名使用 `should_xxx_when_xxx` 格式

```java

@Test
void should_return_user_when_user_exists() {
    // given
    Long userId = 1L;
    User expectedUser = new User(userId, "test");

    // when
    User actualUser = userService.getUserById(userId);

    // then
    assertThat(actualUser).isEqualTo(expectedUser);
}
```

### 文档要求

- 新增功能需要更新相关 README 文档
- 公共 API 必须提供详细的 JavaDoc
- 重要配置项需要在文档中说明用法

## 🔄 代码提交

### 分支策略

- `main`: 主分支，用于发布
- `develop`: 开发分支，用于集成功能
- `feature/xxx`: 功能分支，用于开发新功能
- `bugfix/xxx`: 修复分支，用于修复bug
- `hotfix/xxx`: 热修复分支，用于紧急修复

### 提交信息规范

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

#### 类型说明

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建工具或依赖更新

#### 示例

```
feat(starter-redis): 添加Redis缓存自动配置

- 新增RedisAutoConfiguration类
- 添加缓存配置属性
- 提供默认的RedisTemplate配置

Closes #123
```

### 提交前检查

```bash
# 代码格式检查
mvn spotless:check

# 运行测试
mvn test

# 构建检查
mvn clean compile
```

## 🔀 Pull Request

### 提交 PR 前的准备

1. 确保您的分支是基于最新的 `develop` 分支
2. 完成所有必要的测试
3. 更新相关文档
4. 检查代码格式和规范

### PR 模板

请按照以下模板提交 PR：

```markdown
## 变更描述

简要描述您的变更内容

## 变更类型

- [ ] 新功能
- [ ] Bug修复
- [ ] 性能优化
- [ ] 代码重构
- [ ] 文档更新
- [ ] 其他

## 测试

- [ ] 已添加单元测试
- [ ] 已运行所有测试
- [ ] 测试覆盖率满足要求

## 检查清单

- [ ] 代码符合项目规范
- [ ] 已更新相关文档
- [ ] 提交信息符合规范
- [ ] 已解决所有冲突

## 相关Issue

Closes #123
```

### Code Review

- 所有 PR 都需要至少一个维护者的审批
- 请耐心等待 Review，通常在 2-3 个工作日内完成
- 根据 Review 意见及时修改代码

## 🐛 问题反馈

### 提交Issue前的检查

1. 搜索现有 Issue，避免重复提交
2. 确认问题可以复现
3. 准备详细的问题描述

### Bug Report 模板

```markdown
## Bug描述

简要描述遇到的问题

## 复现步骤

1. 步骤一
2. 步骤二
3. 看到错误

## 期望行为

描述您期望的正确行为

## 实际行为

描述实际发生的行为

## 环境信息

- OS: [e.g. Windows 11]
- Java版本: [e.g. 21]
- 项目版本: [e.g. 0.0.1-SNAPSHOT]

## 错误日志
```

粘贴相关的错误日志

```

### Feature Request 模板
```markdown
## 功能描述
描述您希望添加的功能

## 使用场景
说明这个功能的使用场景和价值

## 解决方案
描述您认为可行的实现方案

## 替代方案
描述其他可能的解决方案
```

## 🤝 社区支持

### 交流渠道

- **GitHub Discussions**: 用于一般讨论和问答
- **GitHub Issues**: 用于bug报告和功能请求
- **邮件列表**: athena-dev@glseven.com

### 贡献者权益

- 突出贡献者将被邀请成为项目维护者
- 定期贡献者可以获得项目相关资源和支持
- 优秀贡献者有机会参与项目规划和决策

### 行为准则

请遵守我们的 [行为准则](CODE_OF_CONDUCT.md)，营造友好、包容的社区环境。

## 📞 联系我们

如果您有任何问题或建议，请通过以下方式联系我们：

- **邮箱**: athena-dev@glseven.com
- **GitHub**: [项目主页](https://github.com/glseven/athena)

---

再次感谢您的贡献！每一个贡献都让 Athena 变得更好。 🚀
