# Maven 中央仓库发布配置指南

## 概述

本文档说明如何将 athena-parent 项目发布到 Maven 中央仓库。

## 前置条件

### 1. 注册 Sonatype OSSRH 账户

1. 访问 [Sonatype JIRA](https://issues.sonatype.org/secure/Signup!default.jspa) 注册账户
2. 创建一个新的项目票据来申请 groupId `com.gls.athena`
3. 等待 Sonatype 团队审核通过

### 2. 生成 GPG 密钥

```bash
# 生成 GPG 密钥对
gpg --gen-key

# 列出已生成的密钥
gpg --list-keys

# 将公钥发布到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 3. 配置 Maven settings.xml

在 `~/.m2/settings.xml` 中添加以下配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          https://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <!-- Sonatype OSSRH 服务器配置 -->
        <server>
            <id>ossrh</id>
            <username>YOUR_SONATYPE_USERNAME</username>
            <password>YOUR_SONATYPE_PASSWORD</password>
        </server>
    </servers>

    <profiles>
        <!-- GPG 签名配置 -->
        <profile>
            <id>gpg</id>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.keyname>YOUR_GPG_KEY_ID</gpg.keyname>
                <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>gpg</activeProfile>
    </activeProfiles>
</settings>
```

## 发布流程

### 1. 发布快照版本

```bash
# 构建并发布快照版本到 OSSRH
mvn clean deploy
```

### 2. 发布正式版本

```bash
# 激活 release profile 并发布
mvn clean deploy -P release

# 或者使用 Maven Release Plugin
mvn release:clean release:prepare release:perform
```

### 3. 版本发布步骤

1. **准备发布**：
   ```bash
   # 更新版本号为正式版本
   mvn versions:set -DnewVersion=1.0.0
   mvn versions:commit
   ```

2. **执行发布**：
   ```bash
   mvn clean deploy -P release
   ```

3. **推送到 Git**：
   ```bash
   git add .
   git commit -m "Release version 1.0.0"
   git tag v1.0.0
   git push origin main
   git push origin v1.0.0
   ```

4. **准备下一个开发版本**：
   ```bash
   mvn versions:set -DnewVersion=1.0.1-SNAPSHOT
   mvn versions:commit
   git add .
   git commit -m "Prepare for next development iteration"
   git push origin main
   ```

## 验证发布

1. 登录 [Nexus Repository Manager](https://oss.sonatype.org/)
2. 检查 Staging Repositories 中的构件
3. 如果一切正常，构件会自动发布到 Maven 中央仓库
4. 通常需要等待 2-4 小时才能在中央仓库中搜索到

## 故障排除

### 常见问题

1. **GPG 签名失败**
    - 确保 GPG 密钥已正确配置
    - 检查 GPG 密钥是否已过期

2. **上传失败**
    - 检查 Sonatype 账户凭据
    - 确保 groupId 已获得授权

3. **POM 验证失败**
    - 确保所有必需的 POM 元素都已填写
    - 检查许可证、开发者信息等

### 有用的命令

```bash
# 检查项目是否准备好发布
mvn enforcer:enforce

# 验证 POM 文件
mvn validate

# 生成站点文档
mvn site

# 检查依赖
mvn dependency:tree
```

## 注意事项

1. 确保项目代码质量，包括测试覆盖率
2. 提供完整的文档和示例
3. 遵循语义版本规范
4. 保持向后兼容性
5. 及时响应用户反馈和问题

## 相关链接

- [OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Signing Guide](https://central.sonatype.org/publish/publish-guide/#gpg-signing)
