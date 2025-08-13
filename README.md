# Athena Parent - 基础工具包

## 📖 项目介绍

Athena Parent 是 Athena 微服务平台的基础工具包，基于 Spring Boot 3.5.0 构建，提供统一的依赖管理、通用工具类、第三方SDK集成和自动配置启动器。本模块采用模块化设计，为整个 Athena 生态系统提供坚实的基础支撑。

## 🏗️ 技术栈

- **Java**: 21
- **Spring Boot**: 3.5.0
- **Spring Cloud**: 2025.0.0
- **Spring AI**: 1.0.0
- **gRPC**: 1.72.0
- **Netflix DGS**: 10.2.1
- **Vaadin**: 24.7.6
- **MapStruct**: 1.6.3

## 📁 项目结构

```
athena-parent/
├── athena-bom/                    # 依赖版本管理（BOM）
└── athena-project/               # 核心项目模块
    ├── athena-cloud/             # 云原生支持
    │   ├── athena-cloud-boot/    # 云原生启动模块
    │   └── athena-cloud-core/    # 云原生核心模块
    ├── athena-common/            # 通用工具包
    │   ├── athena-common-bean/   # 通用Bean工具
    │   └── athena-common-core/   # 通用核心工具
    ├── athena-sdk/              # 第三方SDK集成
    │   ├── athena-sdk-amap/     # 高德地图SDK
    │   ├── athena-sdk-core/     # SDK核心模块
    │   ├── athena-sdk-feishu/   # 飞书SDK
    │   ├── athena-sdk-log/      # 日志SDK
    │   ├── athena-sdk-message/  # 消息SDK
    │   ├── athena-sdk-wechat/   # 微信SDK
    │   └── athena-sdk-xxl-job/  # XXL-Job SDK
    └── athena-starter/          # 自动配置启动器
        ├── athena-starter-aliyun-core/      # 阿里云核心启动器
        ├── athena-starter-aliyun-oss/       # 阿里云OSS启动器
        ├── athena-starter-aliyun-sms/       # 阿里云SMS启动器
        ├── athena-starter-core/             # 核心启动器
        ├── athena-starter-data-jpa/         # JPA数据启动器
        ├── athena-starter-data-redis/       # Redis数据启动器
        ├── athena-starter-dynamic-datasource/ # 动态数据源启动器
        ├── athena-starter-excel/            # Excel处理启动器
        ├── athena-starter-jasper/           # Jasper报表启动器
        ├── athena-starter-json/             # JSON处理启动器
        ├── athena-starter-mybatis/          # MyBatis启动器
        ├── athena-starter-pdf/              # PDF处理启动器
        ├── athena-starter-swagger/          # Swagger文档启动器
        ├── athena-starter-web/              # Web启动器
        └── athena-starter-word/             # Word处理启动器
```

## 🚀 核心模块

### 📦 athena-bom
依赖版本管理模块，统一管理所有第三方依赖的版本，确保版本兼容性。

**主要特性：**
- 统一版本管理
- 避免版本冲突
- 简化依赖引入

### ☁️ athena-cloud
云原生支持模块，提供微服务架构下的基础能力。

**包含模块：**
- **athena-cloud-boot**: 云原生启动支持
- **athena-cloud-core**: 云原生核心功能

**主要特性：**
- 服务发现与注册
- 配置中心集成
- 链路追踪
- 健康检查

### 🔧 athena-common
通用工具包，提供项目中常用的工具类和公共组件。

**包含模块：**
- **athena-common-bean**: Bean操作工具
- **athena-common-core**: 核心工具类

**主要特性：**
- 常用工具类封装
- Bean操作工具
- 类型转换工具
- 验证工具

### 🔌 athena-sdk
第三方服务SDK集成，提供常用第三方服务的集成支持。

**包含SDK：**

| SDK | 描述 | 功能 |
|-----|------|------|
| athena-sdk-amap | 高德地图SDK | 地图服务、地理编码、路径规划 |
| athena-sdk-feishu | 飞书SDK | 企业通讯、消息推送、身份认证 |
| athena-sdk-wechat | 微信SDK | 微信登录、支付、消息推送 |
| athena-sdk-xxl-job | XXL-Job SDK | 分布式任务调度 |
| athena-sdk-message | 消息SDK | 统一消息处理 |
| athena-sdk-log | 日志SDK | 日志统一处理 |
| athena-sdk-core | SDK核心 | SDK通用功能 |

### 🎯 athena-starter
自动配置启动器，提供开箱即用的功能模块。

**数据存储启动器：**
- **athena-starter-data-jpa**: JPA数据访问自动配置
- **athena-starter-data-redis**: Redis缓存自动配置
- **athena-starter-mybatis**: MyBatis持久层自动配置
- **athena-starter-dynamic-datasource**: 动态数据源自动配置

**云服务启动器：**
- **athena-starter-aliyun-core**: 阿里云核心服务配置
- **athena-starter-aliyun-oss**: 阿里云对象存储配置
- **athena-starter-aliyun-sms**: 阿里云短信服务配置

**文档处理启动器：**
- **athena-starter-excel**: Excel文件处理
- **athena-starter-pdf**: PDF文件生成
- **athena-starter-word**: Word文档处理
- **athena-starter-jasper**: Jasper报表生成

**Web功能启动器：**
- **athena-starter-web**: Web应用基础配置
- **athena-starter-swagger**: API文档自动生成
- **athena-starter-json**: JSON处理配置

## 📋 使用指南

### 依赖引入

在项目的 `pom.xml` 中引入BOM：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.gls.athena</groupId>
            <artifactId>athena-bom</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 启动器使用

根据需要引入相应的启动器：

```xml
<dependencies>
    <!-- Web启动器 -->
    <dependency>
        <groupId>com.gls.athena</groupId>
        <artifactId>athena-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis启动器 -->
    <dependency>
        <groupId>com.gls.athena</groupId>
        <artifactId>athena-starter-mybatis</artifactId>
    </dependency>
    
    <!-- Redis启动器 -->
    <dependency>
        <groupId>com.gls.athena</groupId>
        <artifactId>athena-starter-data-redis</artifactId>
    </dependency>
    
    <!-- Excel处理启动器 -->
    <dependency>
        <groupId>com.gls.athena</groupId>
        <artifactId>athena-starter-excel</artifactId>
    </dependency>
</dependencies>
```

### SDK使用示例

**高德地图SDK：**
```java
@Autowired
private AmapService amapService;

// 地理编码
GeoResult result = amapService.geocode("北京市朝阳区");
```

**飞书SDK：**
```java
@Autowired
private FeishuService feishuService;

// 发送消息
feishuService.sendMessage(chatId, "Hello, Feishu!");
```

## ⚙️ 配置说明

### 基础配置
```yaml
athena:
  cloud:
    enabled: true
  common:
    enabled: true
```

### SDK配置
```yaml
athena:
  sdk:
    amap:
      key: your-amap-key
    feishu:
      app-id: your-feishu-app-id
      app-secret: your-feishu-app-secret
    wechat:
      app-id: your-wechat-app-id
      app-secret: your-wechat-app-secret
```

### 启动器配置
```yaml
athena:
  starter:
    excel:
      temp-dir: /tmp/excel
    pdf:
      temp-dir: /tmp/pdf
    aliyun:
      access-key: your-access-key
      secret-key: your-secret-key
      oss:
        endpoint: your-oss-endpoint
        bucket: your-bucket
```

## 🔧 开发指南

### 本地构建
```bash
# 编译整个项目
mvn clean compile

# 打包
mvn clean package

# 安装到本地仓库
mvn clean install
```

### 模块开发
- 新增工具类请放在 `athena-common` 模块
- 新增SDK请在 `athena-sdk` 下创建对应模块
- 新增自动配置请在 `athena-starter` 下创建启动器

### 版本管理
- 所有版本号统一在 `athena-bom` 中管理
- 遵循语义化版本规范
- 发布前确保版本兼容性

## 📈 版本历史

| 版本 | 发布日期 | 主要变更 |
|------|----------|----------|
| 0.0.1-SNAPSHOT | 2025-01-13 | 初始版本 |

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 [LICENSE](LICENSE) 许可证。
