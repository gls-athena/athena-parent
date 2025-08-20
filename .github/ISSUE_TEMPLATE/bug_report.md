---
name: Bug Report
about: 报告一个 Bug 来帮助我们改进项目
title: '[BUG] '
labels: 'bug'
assignees: ''

---

## 🐛 Bug 描述

请简要描述遇到的问题。

## 📍 影响模块

请选择受影响的模块（可多选）：

- [ ] athena-bom (依赖管理)
- [ ] athena-cloud (云原生支持)
- [ ] athena-common (通用工具)
- [ ] athena-sdk (第三方SDK)
- [ ] athena-starter (自动配置启动器)
- [ ] 其他模块: ___________

## 🔄 复现步骤

请提供详细的复现步骤：

1. 添加依赖：
   ```xml
   <dependency>
       <groupId>io.github.gls-athena</groupId>
       <artifactId>模块名称</artifactId>
       <version>版本号</version>
   </dependency>
   ```

2. 配置信息：
   ```yaml
   # application.yml
   相关配置
   ```

3. 执行代码：
   ```java
   // 相关代码
   ```

4. 观察到的错误现象

## ✅ 期望行为

描述您期望的正确行为。

## ❌ 实际行为

描述实际发生的错误行为。

## 📋 环境信息

### 运行环境

- **操作系统**: [如 Windows 11, macOS 13, Ubuntu 22.04]
- **Java 版本**: [如 Java 21]
- **Maven 版本**: [如 3.9.0]
- **IDE**: [如 IntelliJ IDEA 2024.1]

### 项目信息

- **Athena 版本**: [如 0.0.1-SNAPSHOT]
- **Spring Boot 版本**: [如 3.5.0]
- **Spring Cloud 版本**: [如 2025.0.0]

### 相关依赖版本

请列出可能相关的其他依赖及其版本：

```xml
<!-- 如有相关依赖，请提供版本信息 -->
```

## 📝 错误日志

请提供完整的错误堆栈信息：

```
粘贴错误日志/堆栈信息
```

## 📊 配置信息

请提供相关的配置文件内容（请移除敏感信息）：

<details>
<summary>application.yml / application.properties</summary>

```yaml
# 相关配置内容
```

</details>

<details>
<summary>pom.xml 依赖部分</summary>

```xml
<!-- 相关依赖配置 -->
```

</details>

## 🔍 已尝试的解决方案

请描述您已经尝试过的解决方案：

- [ ] 清理并重新构建项目 (`mvn clean install`)
- [ ] 检查依赖冲突
- [ ] 查阅文档和示例
- [ ] 搜索现有 Issue
- [ ] 其他: ___________

## 💡 可能的解决方案

如果您有任何可能的解决方案建议，请在此描述。

## 📎 附加信息

### 截图

如果适用，请添加截图来帮助说明问题。

### 最小可复现示例

如果可能，请提供一个最小的可复现示例项目或代码片段。

### 相关 Issue

如果存在相关的 Issue，请引用：

- Related to #___

---

**检查清单 (请确认以下事项):**

- [ ] 我已经搜索了现有的 Issue，确认这不是重复报告
- [ ] 我已经阅读了项目文档和贡献指南
- [ ] 我已经提供了足够的信息来复现这个问题
- [ ] 我已经移除了敏感信息（如密钥、密码等）
- [ ] 我已经尝试了基本的故障排除步骤

---

感谢您的 Bug 报告！这将帮助我们改进 Athena 项目。🚀
