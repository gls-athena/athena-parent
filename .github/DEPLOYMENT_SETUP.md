# Maven Central 部署配置指南

## 问题解决

您遇到的 401 认证错误是因为缺少正确的 OSSRH 认证配置。我已经修复了以下问题：

1. ✅ 更新了 `settings.xml` 文件，添加了正确的环境变量引用
2. ✅ 修复了 CD 工作流的认证配置
3. ✅ 改进了部署逻辑，支持 SNAPSHOT 和 Release 版本
4. ✅ 使用了更新的 GitHub Release action

## 必需的 GitHub Secrets

在您的 GitHub 仓库设置中，需要配置以下 Secrets：

### OSSRH 认证 (必需)

```
OSSRH_USERNAME: 您的 Sonatype OSSRH 用户名
OSSRH_PASSWORD: 您的 Sonatype OSSRH 密码或 Token
```

### GPG 签名 (Release 版本必需)

```
GPG_PRIVATE_KEY: GPG 私钥（Base64 编码或 ASCII armor 格式）
GPG_PASSPHRASE: GPG 私钥密码短语
GPG_KEYNAME: GPG 密钥 ID（可选，格式如：0x1234567890ABCDEF）
```

## 获取 OSSRH 凭据

1. **注册 OSSRH 账号**
    - 访问 https://issues.sonatype.org
    - 创建账号并申请项目命名空间
    - 等待审核通过

2. **生成用户令牌**
    - 登录 https://oss.sonatype.org
    - 点击右上角用户名 → Profile
    - 在 User Token 部分点击 "Access User Token"
    - 复制 Username 和 Password

## 设置 GPG 密钥

### 生成 GPG 密钥

```bash
# 生成新的 GPG 密钥
gpg --gen-key

# 列出密钥
gpg --list-secret-keys --keyid-format LONG

# 导出私钥（用于 GitHub Secret）
gpg --armor --export-secret-keys YOUR_KEY_ID

# 导出公钥（上传到密钥服务器）
gpg --armor --export YOUR_KEY_ID
```

### 上传公钥到密钥服务器

```bash
# 上传到多个密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
gpg --keyserver pgp.mit.edu --send-keys YOUR_KEY_ID
```

## GitHub Secrets 配置步骤

1. 进入 GitHub 仓库
2. 点击 Settings → Secrets and variables → Actions
3. 点击 "New repository secret"
4. 添加上述所有必需的 secrets

## 验证配置

配置完成后，您可以：

1. **测试 SNAPSHOT 部署**
   ```bash
   # 手动触发工作流，选择 patch 版本
   # 或者推送到 master 分支
   ```

2. **测试 Release 部署**
   ```bash
   # 确保版本号不包含 -SNAPSHOT
   # 手动触发工作流
   ```

## 常见问题排查

### 401 认证错误

- ✅ 检查 OSSRH_USERNAME 和 OSSRH_PASSWORD 是否正确
- ✅ 确认使用的是用户令牌而非原始密码
- ✅ 验证 settings.xml 中的服务器 ID 匹配

### GPG 签名错误

- ✅ 确认 GPG_PRIVATE_KEY 格式正确
- ✅ 检查 GPG_PASSPHRASE 是否匹配
- ✅ 验证公钥已上传到密钥服务器

### 网络连接问题

- ✅ GitHub Actions 网络限制
- ✅ Maven Central 服务状态
- ✅ 重试机制

## 部署流程说明

1. **SNAPSHOT 版本**：自动部署到 OSSRH Snapshots 仓库
2. **Release 版本**：部署到 Maven Central，需要 GPG 签名
3. **版本管理**：自动计算下一个开发版本
4. **Git 操作**：自动创建标签和 GitHub Release

## 监控和日志

- 查看 GitHub Actions 日志获取详细错误信息
- 检查 OSSRH 仓库中的部署状态
- 验证 Maven Central 中的发布状态

---

**重要提示**：确保所有 Secrets 都正确配置后再次运行部署流程。
