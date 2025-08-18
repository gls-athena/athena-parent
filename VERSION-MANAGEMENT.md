# Maven版本管理优化 - flatten-maven-plugin 使用指南

## 概述

本项目已成功优化了Maven配置，使用flatten-maven-plugin实现CI友好的版本管理。所有33个模块现在都支持统一的版本号管理和动态版本设置。

## 优化内容

### 1. CI友好的版本占位符

- 所有POM文件的version标签已改为：`${revision}`
- 父POM中定义版本属性：
  ```xml
  <properties>
      <revision>0.0.1-SNAPSHOT</revision>
  </properties>
  ```

### 2. flatten-maven-plugin配置

```xml

<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <version>1.6.0</version>
    <configuration>
        <updatePomFile>true</updatePomFile>
        <flattenMode>resolveCiFriendliesOnly</flattenMode>
    </configuration>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
        <execution>
            <id>flatten.clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 使用方法

### 1. 基本构建

```bash
# 使用默认版本号 (0.0.1-SNAPSHOT)
mvn clean install
```

### 2. 动态版本号构建

```bash
# 发布版本
mvn clean install -Drevision=1.0.0

# RC版本
mvn clean install -Drevision=1.0.0 -Dchangelist=-RC1

# 带构建号的版本
mvn clean install -Drevision=1.0.0 -Dchangelist=-RC1 -Dsha1=.20250818

# SNAPSHOT版本
mvn clean install -Drevision=1.1.0 -Dchangelist=-SNAPSHOT
```

### 3. CI/CD集成示例

```yaml
# GitHub Actions 示例
- name: Build with dynamic version
  run: |
    VERSION="1.0.0"
    BUILD_NUMBER="${{ github.run_number }}"
    mvn clean install -Drevision=${VERSION} -Dchangelist=-RC -Dsha1=.${BUILD_NUMBER}
```

### 4. 验证flatten插件

```bash
# 生成扁平化POM文件
mvn flatten:flatten

# 清理扁平化文件
mvn flatten:clean
```

## 项目结构

优化后的项目包含33个模块：

```
athena-parent (根模块)
├── athena-bom (依赖管理)
├── athena-project (项目聚合)
    ├── athena-cloud (云服务模块)
    │   ├── athena-cloud-boot
    │   └── athena-cloud-core
    ├── athena-common (通用模块)
    │   ├── athena-common-bean
    │   └── athena-common-core
    ├── athena-sdk (SDK模块)
    │   ├── athena-sdk-amap
    │   ├── athena-sdk-core
    │   ├── athena-sdk-feishu
    │   ├── athena-sdk-log
    │   ├── athena-sdk-message
    │   ├── athena-sdk-wechat
    │   └── athena-sdk-xxl-job
    └── athena-starter (启动器模块)
        ├── athena-starter-aliyun-core
        ├── athena-starter-aliyun-oss
        ├── athena-starter-aliyun-sms
        ├── athena-starter-core
        ├── athena-starter-data-jpa
        ├── athena-starter-data-redis
        ├── athena-starter-dynamic-datasource
        ├── athena-starter-excel
        ├── athena-starter-jasper
        ├── athena-starter-json
        ├── athena-starter-mybatis
        ├── athena-starter-pdf
        ├── athena-starter-swagger
        ├── athena-starter-web
        └── athena-starter-word
```

## 优势

### 1. 版本管理统一

- 所有模块版本号完全同步
- 避免了手动更新多个POM文件的版本号
- 减少了版本不一致的风险

### 2. CI/CD友好

- 支持运行时动态设置版本号
- 可以根据分支、构建号等生成不同的版本
- 便于自动化部署和发布

### 3. 构建优化

- flatten-maven-plugin确保发布的POM文件是扁平化的
- 消费者不需要关心内部的版本占位符
- 提高了构建的可靠性和一致性

## 注意事项

1. **版本占位符必须连续使用**：`${revision}`
2. **父POM必须定义所有占位符属性**，即使某些为空
3. **flatten插件会在process-resources阶段生成.flattened-pom.xml文件**
4. **清理时会自动删除扁平化文件**

## 故障排除

### 常见问题

1. **构建失败**：确保所有POM文件都使用了正确的版本占位符格式
2. **版本解析错误**：检查父POM中是否正确定义了revision、changelist、sha1属性
3. **依赖解析问题**：确保BOM文件正确导入并使用${revision}

### 验证方法

```bash
# 验证项目结构和配置
mvn validate

# 检查有效POM
mvn help:effective-pom

# 生成依赖树
mvn dependency:tree
```

## 更新历史

- **2025-08-18**: 完成所有33个模块的版本管理优化
- 实施CI友好的版本占位符
- 配置flatten-maven-plugin
- 验证构建和插件功能正常

---

通过这次优化，项目的版本管理变得更加灵活和可维护，为后续的持续集成和持续部署奠定了良好的基础。
