# Athena PDF Starter 使用指南

## 概述

Athena PDF Starter 提供了一个基于注解的PDF文档生成功能，支持HTML模板渲染和默认数据生成，基于OpenPDF实现。

## 核心特性

- 🚀 **注解驱动**: 使用 `@PdfResponse` 注解轻松导出PDF文档
- 📄 **多模板支持**: 支持HTML模板和纯数据生成
- 🎨 **自定义生成器**: 支持自定义PDF生成器实现
- 📱 **内联显示**: 支持浏览器内查看或下载
- ⚡ **职责单一**: 清晰的分层架构，易于扩展和维护
- 🎯 **自动配置**: Spring Boot自动配置，开箱即用

## 架构设计

### 核心组件

1. **PdfGenerator**: PDF生成器接口
    - `HtmlPdfGenerator`: HTML模板生成器（基于OpenPDF + 飞行渲染器）
    - `DefaultPdfGenerator`: 默认数据生成器（基于OpenPDF）

2. **PdfGeneratorManager**: 生成器管理器，负责选择合适的生成器

3. **PdfResponseHandler**: HTTP响应处理器，拦截@PdfResponse注解

4. **PdfProperties**: 配置属性类

## 快速开始

### 1. 添加依赖

```xml

<dependency>
    <groupId>com.gls.athena.starter.pdf</groupId>
    <artifactId>athena-starter-pdf</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置属性（可选）

```yaml
athena:
  pdf:
    default-template-path: classpath:templates/pdf/
    default-file-prefix: document
    cache-enabled: true
    cache-size: 100
    page-settings:
      page-size: A4
      orientation: PORTRAIT
      margin-top: 20
      margin-bottom: 20
      margin-left: 20
      margin-right: 20
```

### 3. 使用方式

#### 方式一：默认生成（无模板）

```java

@RestController
public class ReportController {

    @GetMapping("/export/user")
    @PdfResponse(fileName = "用户报告")
    public Map<String, Object> exportUser() {
        return Map.of(
                "用户名", "张三",
                "年龄", 25,
                "部门", "技术部"
        );
    }
}
```

#### 方式二：HTML模板生成

```java

@GetMapping("/export/employee")
@PdfResponse(
        fileName = "员工报告",
        template = "employee-report.html",
        templateType = TemplateType.HTML
)
public EmployeeData exportEmployee() {
    return new EmployeeData("李四", 28, "开发工程师");
}
```

#### 方式三：自定义生成器

```java

@GetMapping("/export/report")
@PdfResponse(
        fileName = "销售报表",
        generator = ReportStylePdfGenerator.class
)
public Map<String, Object> exportReport() {
    return salesData;
}
```

#### 方式四：内联显示

```java

@GetMapping("/view/catalog")
@PdfResponse(
        fileName = "产品目录",
        inline = true  // 浏览器内查看
)
public List<ProductData> viewCatalog() {
    return productList;
}
```

## 模板制作

### HTML模板语法

支持简单的变量替换语法：

- `{{variable}}`: 简单变量替换
- `{{object.property}}`: 嵌套属性访问

### HTML模板示例

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工报告</title>
    <style>
        body {
            font-family: 'SimSun', serif;
        }

        .header {
            text-align: center;
        }

        .info-table {
            width: 100%;
            border-collapse: collapse;
        }

        .info-table th, .info-table td {
            border: 1px solid #ddd;
            padding: 12px;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>员工信息报告</h1>
</div>

<table class="info-table">
    <tr>
        <td>姓名</td>
        <td>{{name}}</td>
    </tr>
    <tr>
        <td>年龄</td>
        <td>{{age}}</td>
    </tr>
    <tr>
        <td>职位</td>
        <td>{{position}}</td>
    </tr>
</table>
</body>
</html>
```

## 扩展开发

### 自定义生成器

```java

@Component
public class CustomPdfGenerator implements PdfGenerator {

    @Override
    public void generate(Object data, String template, TemplateType templateType,
                         OutputStream outputStream) throws Exception {
        // 自定义生成逻辑
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // 添加内容...

        document.close();
    }

    @Override
    public boolean supports(TemplateType templateType) {
        return templateType == TemplateType.DATA;
    }
}
```

## 注解属性说明

| 属性           | 类型           | 默认值                | 说明                   |
|--------------|--------------|--------------------|----------------------|
| fileName     | String       | ""                 | 文件名，为空时自动生成          |
| template     | String       | ""                 | 模板路径                 |
| generator    | Class        | PdfGenerator.class | 自定义生成器类              |
| templateType | TemplateType | AUTO               | 模板类型（AUTO/HTML/DATA） |
| inline       | boolean      | false              | 是否内联显示               |

## 模板类型说明

- **AUTO**: 自动检测，根据模板文件扩展名判断
- **HTML**: HTML模板类型，使用HtmlPdfGenerator
- **DATA**: 纯数据类型，使用DefaultPdfGenerator

## 配置说明

| 属性                        | 默认值                      | 说明      |
|---------------------------|--------------------------|---------|
| default-template-path     | classpath:templates/pdf/ | 默认模板路径  |
| temp-path                 | 系统临时目录                   | 临时文件路径  |
| default-file-prefix       | document                 | 默认文件名前缀 |
| cache-enabled             | true                     | 是否启用缓存  |
| cache-size                | 100                      | 缓存大小    |
| page-settings.page-size   | A4                       | 页面大小    |
| page-settings.orientation | PORTRAIT                 | 页面方向    |

## 最佳实践

1. **模板管理**: 将HTML模板放在 `src/main/resources/templates/pdf/` 目录下
2. **字体支持**: OpenPDF支持中文字体，确保CSS中指定正确的字体
3. **性能优化**: 启用缓存来提高模板加载性能
4. **错误处理**: 生成失败时会返回JSON错误信息
5. **文件命名**: 使用有意义的文件名，支持中文

## 注意事项

1. HTML模板必须是有效的XHTML格式
2. CSS样式需要内联或在`<style>`标签中定义
3. 不支持JavaScript
4. 图片需要使用绝对路径或base64编码
5. 表格和布局推荐使用CSS Grid或Flexbox

## 依赖版本

- OpenPDF: 1.3.30
- Spring Boot: 兼容当前项目版本
- Jackson: 用于数据转换

## 示例项目

查看 `PdfExampleController` 类获取完整的使用示例，包括：

- 默认数据生成
- HTML模板渲染
- 自定义生成器
- 内联显示
- 类级别注解
