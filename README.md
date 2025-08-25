# Athena Parent - 企业级微服务基础平台

<div align="center">

![GitHub license](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.gls-athena/athena-parent)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.1-blue)
![Java](https://img.shields.io/badge/Java-21+-orange)
![gRPC](https://img.shields.io/badge/gRPC-1.72.0-blue)

**现代化微服务基础平台，提供企业级开发工具包和统一依赖管理**

[快速开始](#-快速开始) • [核心功能](#-核心功能) • [项目架构](#-项目架构) • [使用指南](#-使用指南) • [最佳实践](#-最佳实践)

</div>

## 📖 项目介绍

Athena Parent 是 Athena 微服务生态系统的核心基础平台，基于 Spring Boot 3.5.4 和 Spring Cloud 2025.0.0 构建。项目采用现代化的架构设计，集成了云原生、AI、消息队列、第三方服务等企业级功能模块，为微服务应用开发提供统一的依赖管理、通用工具类和自动配置启动器。

## ✨ 核心功能

### 🏗️ 基础架构

- **统一依赖管理** - 通过 BOM 统一管理所有模块版本，避免冲突
- **模块化设计** - 高度模块化架构，按需集成，降低耦合
- **云原生支持** - 原生支持容器化部署和云环境
- **自动配置** - 丰富的 Spring Boot Starter，开箱即用

### 🤖 AI 集成

- **Spring AI 集成** - 基于 Spring AI 1.0.1 的人工智能能力
- **多模型支持** - 支持主流 AI 模型接入
- **智能决策** - 提供智能化的业务决策支持

### 🌐 通信协议

- **gRPC 支持** - 高性能 RPC 通信，支持多语言互操作
- **RESTful API** - 完整的 REST API 支持
- **GraphQL** - 基于 Netflix DGS 的 GraphQL 集成
- **WebSocket** - 实时通信支持

### 🔗 第三方集成

- **高德地图** - 地理位置和地图服务集成
- **微信生态** - 微信公众号、小程序、企业微信集成
- **飞书办公** - 飞书企业应用集成
- **阿里云服务** - 阿里云全家桶服务集成

### 📊 监控运维

- **链路追踪** - 分布式链路追踪和性能监控
- **日志管理** - 统一日志收集、存储和分析
- **健康检查** - 全面的应用健康检查机制
- **指标监控** - 丰富的业务和技术指标监控

## 🏗️ 项目架构

```
athena-parent/
├── athena-bom/                                    # 📦 依赖管理BOM
│   └── pom.xml                                   # 统一版本管理
├── athena-project/                               # 🏗️ 核心项目模块
│   ├── athena-cloud/                            # ☁️ 云原生模块
│   │   ├── athena-cloud-boot/                   # 云原生启动器
│   │   └── athena-cloud-core/                   # 云原生核心组件
│   ├── athena-common/                           # 🔧 通用工具模块
│   │   ├── athena-common-bean/                  # 通用Bean定义
│   │   └── athena-common-core/                  # 核心工具类
│   ├── athena-sdk/                              # 🛠️ 第三方SDK模块
│   │   ├── athena-sdk-amap/                     # 🗺️ 高德地图SDK
│   │   ├── athena-sdk-core/                     # SDK核心组件
│   │   ├── athena-sdk-feishu/                   # 🟦 飞书SDK
│   │   ├── athena-sdk-log/                      # 📝 日志SDK
│   │   ├── athena-sdk-message/                  # 💬 消息SDK
│   │   ├── athena-sdk-wechat/                   # 🟢 微信SDK
│   │   └── athena-sdk-xxl-job/                  # ⚡ XXL-JOB SDK
│   └── athena-starter/                          # 🚀 自动配置启动器
│       ├── athena-starter-aliyun-*/             # 阿里云服务启动器
│       ├── athena-starter-captcha/              # 验证码启动器
│       ├── athena-starter-data-*/               # 数据访问启动器
│       ├── athena-starter-graphql/              # GraphQL启动器
│       ├── athena-starter-grpc/                 # gRPC启动器
│       ├── athena-starter-json/                 # JSON处理启动器
│       ├── athena-starter-cache/                # 缓存启动器
│       ├── athena-starter-mq/                   # 消息队列启动器
│       ├── athena-starter-oss/                  # 对象存储启动器
│       ├── athena-starter-sentry/               # 监控启动器
│       └── athena-starter-web/                  # Web应用启动器
├── CODE_OF_CONDUCT.md                           # 行为准则
├── CONTRIBUTING.md                              # 贡献指南
├── LICENSE                                      # Apache 2.0 许可证
└── SECURITY.md                                  # 安全政策
```

## 🚀 快速开始

### 环境要求

- ☕ **Java 21+**
- 🌱 **Spring Boot 3.5.x**
- ☁️ **Spring Cloud 2025.0.x**
- 📦 **Maven 3.8+** 或 **Gradle 8.0+**
- 🐳 **Docker** (可选，用于容器化部署)

### Maven 依赖管理

在你的项目根目录 `pom.xml` 中引入 Athena BOM：

```xml

<dependencyManagement>
    <dependencies>
        <!-- Athena BOM 依赖管理 -->
        <dependency>
            <groupId>io.github.gls-athena</groupId>
            <artifactId>athena-bom</artifactId>
            <version>0.0.8</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <!-- Spring Cloud 依赖管理 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2025.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 基础使用

#### 1. Web 应用快速启动

```xml

<dependencies>
    <!-- Web 应用启动器 -->
    <dependency>
        <groupId>io.github.gls-athena.starter.web</groupId>
        <artifactId>athena-starter-web</artifactId>
    </dependency>

    <!-- 通用核心组件 -->
    <dependency>
        <groupId>io.github.gls-athena.common.core</groupId>
        <artifactId>athena-common-core</artifactId>
    </dependency>
</dependencies>
```

#### 2. 云原生应用

```xml

<dependencies>
    <!-- 云原生启动器 -->
    <dependency>
        <groupId>io.github.gls-athena.cloud.boot</groupId>
        <artifactId>athena-cloud-boot</artifactId>
    </dependency>

    <!-- 服务发现 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-consul-discovery</artifactId>
    </dependency>
</dependencies>
```

#### 3. gRPC 服务

```xml

<dependencies>
    <!-- gRPC 启动器 -->
    <dependency>
        <groupId>io.github.gls-athena.starter.grpc</groupId>
        <artifactId>athena-starter-grpc</artifactId>
    </dependency>
</dependencies>
```

## 📚 使用指南

### 第三方服务集成

#### 微信服务集成

```xml

<dependency>
    <groupId>io.github.gls-athena.sdk.wechat</groupId>
    <artifactId>athena-sdk-wechat</artifactId>
</dependency>
```

```yaml
# application.yml
athena:
  sdk:
    wechat:
      app-id: ${WECHAT_APP_ID}
      app-secret: ${WECHAT_APP_SECRET}
      miniprogram:
        enabled: true
      official-account:
        enabled: true
```

#### 高德地图集成

```xml

<dependency>
    <groupId>io.github.gls-athena.sdk.amap</groupId>
    <artifactId>athena-sdk-amap</artifactId>
</dependency>
```

```yaml
athena:
  sdk:
    amap:
      key: ${AMAP_API_KEY}
      secret: ${AMAP_API_SECRET}
```

#### 飞书集成

```xml

<dependency>
    <groupId>io.github.gls-athena.sdk.feishu</groupId>
    <artifactId>athena-sdk-feishu</artifactId>
</dependency>
```

### 数据访问配置

#### Redis 配置

```xml

<dependency>
    <groupId>io.github.gls-athena.starter.data.redis</groupId>
    <artifactId>athena-starter-data-redis</artifactId>
</dependency>
```

```yaml
athena:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD}
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
```

#### 数据库配置

```xml

<dependency>
    <groupId>io.github.gls-athena.starter.data.mybatis</groupId>
    <artifactId>athena-starter-data-mybatis</artifactId>
</dependency>
```

### GraphQL 配置

```xml

<dependency>
    <groupId>io.github.gls-athena.starter.graphql</groupId>
    <artifactId>athena-starter-graphql</artifactId>
</dependency>
```

```java

@DgsComponent
public class BookDataFetcher {

    @DgsQuery
    public List<Book> books(@InputArgument String titleFilter) {
        // 查询逻辑
        return bookService.findByTitle(titleFilter);
    }

    @DgsMutation
    public Book addBook(@InputArgument BookInput book) {
        // 添加书籍逻辑
        return bookService.save(book);
    }
}
```

### 消息队列集成

```xml

<dependency>
    <groupId>io.github.gls-athena.starter.mq</groupId>
    <artifactId>athena-starter-mq</artifactId>
</dependency>
```

```java

@Component
public class OrderEventListener {

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // 处理订单创建事件
        log.info("Order created: {}", event.getOrderId());
    }
}
```

## 🔧 高级配置

### 自定义 Starter

```java

@Configuration
@ConditionalOnProperty(prefix = "athena.custom", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(CustomProperties.class)
public class CustomAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CustomService customService(CustomProperties properties) {
        return new CustomServiceImpl(properties);
    }
}
```

### 云原生配置

```yaml
# application-cloud.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

spring:
  cloud:
    consul:
      discovery:
        enabled: true
        service-name: ${spring.application.name}
        health-check-interval: 10s
```

### gRPC 服务定义

```protobuf
// user.proto
syntax = "proto3";

package athena.user;

service UserService {
  rpc GetUser(GetUserRequest) returns (GetUserResponse);
  rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
}

message GetUserRequest {
  string user_id = 1;
}

message GetUserResponse {
  User user = 1;
}
```

```java

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        User user = userService.findById(request.getUserId());
        GetUserResponse response = GetUserResponse.newBuilder()
                .setUser(user)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

## 🏆 最佳实践

### 项目结构建议

```
your-project/
├── src/main/java/
│   ├── config/              # 配置类
│   ├── controller/          # REST控制器
│   ├── service/            # 业务服务
│   ├── repository/         # 数据访问层
│   ├── grpc/              # gRPC服务
│   └── graphql/           # GraphQL解析器
├── src/main/resources/
│   ├── application.yml     # 主配置文件
│   ├── application-dev.yml # 开发环境配置
│   └── schema.graphqls     # GraphQL模式
└── src/test/              # 测试代码
```

### 配置管理

```yaml
# 环境变量配置
athena:
  profile: ${ATHENA_PROFILE:dev}

# 功能开关
features:
  graphql: ${ENABLE_GRAPHQL:true}
  grpc: ${ENABLE_GRPC:false}
  ai: ${ENABLE_AI:false}

# 第三方服务
integrations:
  wechat:
    enabled: ${WECHAT_ENABLED:false}
  feishu:
    enabled: ${FEISHU_ENABLED:false}
  amap:
    enabled: ${AMAP_ENABLED:false}
```

### 监控和日志

```yaml
# 日志配置
logging:
  level:
    io.github.gls.athena: INFO
    org.springframework.graphql: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# 监控配置
management:
  tracing:
    sampling:
      probability: 1.0
  metrics:
    tags:
      application: ${spring.application.name}
```

## 📖 技术栈

| 组件               | 版本       | 说明        |
|------------------|----------|-----------|
| **Java**         | 21+      | 基础运行环境    |
| **Spring Boot**  | 3.5.4    | 应用框架      |
| **Spring Cloud** | 2025.0.0 | 微服务框架     |
| **Spring AI**    | 1.0.1    | AI集成框架    |
| **gRPC**         | 1.72.0   | 高性能RPC    |
| **Netflix DGS**  | 10.2.1   | GraphQL框架 |
| **Vaadin**       | 24.7.6   | Web UI框架  |
| **MapStruct**    | 1.6.3    | 对象映射      |
| **Sentry**       | 8.16.0   | 错误监控      |
| **Timefold**     | 1.25.0   | 规划求解      |

## 🚀 部署指南

### Docker 部署

```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Kubernetes 部署

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: athena-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: athena-app
  template:
    metadata:
      labels:
        app: athena-app
    spec:
      containers:
        - name: athena-app
          image: your-registry/athena-app:latest
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "k8s"
```

## 🤝 贡献指南

我们欢迎所有形式的贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详细的贡献指南。

### 开发流程

1. **Fork 项目** - 点击右上角 Fork 按钮
2. **克隆代码** - `git clone https://github.com/yourusername/athena-parent.git`
3. **创建分支** - `git checkout -b feature/amazing-feature`
4. **提交更改** - `git commit -m 'Add some amazing feature'`
5. **推送分支** - `git push origin feature/amazing-feature`
6. **提交 PR** - 在 GitHub 上创建 Pull Request

### 代码规范

- 遵循 Java 编码规范
- 添加必要的单元测试
- 更新相关文档
- 通过所有 CI 检查

## 📄 许可证

本项目采用 [Apache 2.0](LICENSE) 许可证开源。

## 🔒 安全政策

如果您发现安全漏洞，请查看 [SECURITY.md](SECURITY.md) 了解如何负责任地报告。

## 📞 支持与反馈

- 📧 **邮箱**: athena@gls.com
- 🐛 **Issues**: [GitHub Issues](https://github.com/gls-athena/athena-parent/issues)
- 📚 **文档**: [在线文档](https://docs.athena-framework.com)
- 💬 **讨论**: [GitHub Discussions](https://github.com/gls-athena/athena-parent/discussions)

## 🙏 致谢

感谢所有为 Athena Parent 项目做出贡献的开发者和组织！

特别感谢以下开源项目：

- [Spring Framework](https://spring.io/)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [gRPC](https://grpc.io/)
- [Netflix DGS](https://netflix.github.io/dgs/)

---

<div align="center">

**[⬆ 回到顶部](#athena-parent---企业级微服务基础平台)**

Made with ❤️ by [GLS Athena Team](https://github.com/gls-athena)

</div>
