# Athena Word Starter 使用指南

## 概述

Athena Word Starter 提供了一个基于注解的Word文档生成功能，支持模板填充和默认文档生成。

## 核心特性

- 🚀 **注解驱动**: 使用 `@WordResponse` 注解轻松导出Word文档
- 📄 **模板支持**: 基于POI-TL的模板引擎，支持复杂模板填充
- 🔧 **默认生成**: 无模板时自动生成基础Word文档
- ⚡ **职责单一**: 清晰的分层架构，易于扩展和维护
- 🎯 **自动配置**: Spring Boot自动配置，开箱即用

## 架构设计

### 核心组件

1. **WordGenerator**: 文档生成器接口
    - `TemplateWordGenerator`: 模板生成器（基于POI-TL）
    - `DefaultWordGenerator`: 默认生成器（基于POI）

2. **WordGeneratorManager**: 生成器管理器，负责选择合适的生成器

3. **WordResponseHandler**: HTTP响应处理器，拦截@WordResponse注解

4. **WordProperties**: 配置属性类

## 快速开始

### 1. 添加依赖

```xml

<dependency>
    <groupId>com.gls.athena.starter.word</groupId>
    <artifactId>athena-starter-word</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置属性（可选）

```yaml
athena:
  word:
    default-template-path: classpath:templates/word/
    default-file-prefix: document
    cache-enabled: true
    cache-size: 100
```

### 3. 使用方式

#### 方式一：默认生成（无模板）

```java

@RestController
public class ReportController {

    @GetMapping("/export/user")
    @WordResponse(fileName = "用户报告")
    public Map<String, Object> exportUser() {
        return Map.of(
                "userName", "张三",
                "userAge", 25,
                "department", "技术部"
        );
    }
}
```

#### 方式二：模板生成

```java

@GetMapping("/export/employee")
@WordResponse(
        fileName = "员工报告",
        template = "employee-template.docx"
)
public EmployeeData exportEmployee() {
    return new EmployeeData("李四", 28, "开发工程师");
}
```

#### 方式三：类级别注解

```java

@WordResponse(fileName = "默认报告")
@RestController
public class DefaultController {

    @GetMapping("/export")
    public SomeData export() {
        return new SomeData();
    }
}
```

## 模板制作

### POI-TL模板语法

在Word模板中使用以下语法：

- `{{name}}`: 简单变量替换
- `{{#list}}{{item}}{{/list}}`: 列表循环
- `{{%condition}}content{{/condition}}`: 条件渲染

### 模板示例

创建 `employee-template.docx` 模板：

```
员工信息报告

姓名：{{name}}
年龄：{{age}}
职位：{{position}}
部门：{{department}}

技能列表：
{{#skills}}
• {{.}}
{{/skills}}

绩效评价：
{{#performance}}
{{@key}}年度：{{.}}
{{/performance}}
```

## 扩展开发

### 自定义生成器

```java

@Component
public class CustomWordGenerator implements WordGenerator {

    @Override
    public void generate(Object data, String template, OutputStream outputStream) throws Exception {
        // 自定义生成逻辑
    }

    @Override
    public boolean supports(String template) {
        return template != null && template.endsWith(".custom");
    }
}
```

### 自定义处理器

```java

@Component
public class CustomWordResponseHandler implements HandlerMethodReturnValueHandler {
    // 自定义处理逻辑
}
```

## 最佳实践

1. **模板管理**: 将模板文件放在 `src/main/resources/templates/word/` 目录下
2. **错误处理**: 生成失败时会返回JSON错误信息
3. **性能优化**: 启用缓存来提高模板加载性能
4. **文件命名**: 使用有意义的文件名，支持中文

## 配置说明

| 属性                    | 默认值                       | 说明      |
|-----------------------|---------------------------|---------|
| default-template-path | classpath:templates/word/ | 默认模板路径  |
| temp-path             | 系统临时目录                    | 临时文件路径  |
| default-file-prefix   | document                  | 默认文件名前缀 |
| cache-enabled         | true                      | 是否启用缓存  |
| cache-size            | 100                       | 缓存大小    |

## 注意事项

1. 模板文件必须是 `.docx` 格式
2. 数据对象会自动转换为Map格式
3. 生成的文档会直接输出到HTTP响应流
4. 支持中文文件名和内容
5. 异常情况下会返回500状态码和错误信息

## 依赖版本

- Apache POI: 5.2.4
- POI-TL: 1.12.2
- Spring Boot: 兼容当前项目版本
