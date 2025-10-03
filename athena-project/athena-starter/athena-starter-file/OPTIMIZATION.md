# 文件插件优化说明

## 优化概览

本次优化主要针对 `athena-starter-file` 文件插件进行了全面改进，提升了代码质量、性能和可维护性。

## 优化内容

### 1. 异常处理优化

#### 新增 FileException 异常体系

- **位置**: `com.gls.athena.starter.file.exception.FileException`
- **改进**:
    - 创建了统一的文件异常类，包含错误码
    - 定义了多个子异常类：
        - `FileNotFoundException` - 文件不存在
        - `FileReadException` - 文件读取异常
        - `FileWriteException` - 文件写入异常
        - `FileDeleteException` - 文件删除异常
        - `FileValidationException` - 文件验证异常
        - `GeneratorNotFoundException` - 生成器未找到异常
    - 提供了更明确的异常信息和错误追踪

### 2. FileManager 优化

#### 改进点：

- **日志增强**: 添加了详细的日志记录，方便问题排查
- **异常处理**: 改进异常处理机制，保留异常链，避免信息丢失
- **错误信息**: 提供更明确的错误信息，包含文件ID和路径
- **新增方法**: 添加 `deleteFile()` 方法，支持文件删除
- **空值检查**: 增强了空值检查，避免NPE

#### 示例代码：

```java
public OutputStream getOutputStream(String fileId) throws IOException {
    FileInfo fileInfo = fileInfoManager.getFileInfo(fileId);
    if (fileInfo == null) {
        log.warn("未找到文件信息: fileId={}", fileId);
        throw new IllegalArgumentException("文件信息不存在: " + fileId);
    }

    try {
        return fileStorageManager.getOutputStream(fileInfo.getFilePath());
    } catch (IOException e) {
        log.error("获取文件输出流失败: fileId={}, filePath={}", fileId, fileInfo.getFilePath(), e);
        throw new IOException("获取文件输出流失败: " + e.getMessage(), e);
    }
}
```

### 3. DefaultFileStorageManager 优化

#### 改进点：

- **日志记录**: 所有关键操作都添加了日志
- **异常处理**: 统一异常处理，保留异常原因
- **URL生成**: 优化URL生成逻辑，正确处理路径分隔符
- **空值检查**: 处理配置未设置的情况

#### URL生成优化：

```java

@Override
public String generateFileUrl(String filePath, Date expiresTime) {
    String urlPrefix = fileProperties.getUrlPrefix();
    if (urlPrefix == null || urlPrefix.isEmpty()) {
        log.warn("URL前缀未配置，返回空字符串");
        return "";
    }

    // 规范化URL路径
    String normalizedPath = filePath.replace(File.separator, "/");
    String url = urlPrefix.endsWith("/")
            ? urlPrefix + normalizedPath
            : urlPrefix + "/" + normalizedPath;

    log.debug("生成文件URL: {}", url);
    return url;
}
```

### 4. FileResponseHandler 优化

#### 改进点：

- **异常处理**: 使用自定义异常类，提供更好的错误信息
- **方法提取**: 提取 `findSupportedGenerator()` 方法，提高代码可读性
- **日志增强**: 添加详细的日志记录
- **空值检查**: 增强空值检查

### 5. FileAsyncAspect 优化

#### 改进点：

- **异常处理**: 使用自定义异常类
- **日志增强**: 在关键步骤添加调试日志
- **错误信息**: 改进错误消息处理，避免空消息

### 6. 新增文件清理功能

#### FileCleanupTask

- **位置**: `com.gls.athena.starter.file.task.FileCleanupTask`
- **功能**:
    - 定时清理过期文件（默认保留7天）
    - 清理空目录
    - 可配置执行时间和保留天数
    - 支持启用/禁用

#### 配置示例：

```yaml
athena:
  file:
    cleanup:
      enabled: true              # 启用文件清理
      cron: "0 0 2 * * ?"       # 每天凌晨2点执行
      retention-days: 7          # 文件保留7天
```

### 7. FileProperties 扩展

#### 新增配置：

```java
/**
 * 文件清理配置
 */
private Cleanup cleanup = new Cleanup();

@Data
public static class Cleanup {
    /**
     * 是否启用文件清理任务
     */
    private boolean enabled = false;

    /**
     * 清理任务执行的cron表达式
     * 默认每天凌晨2点执行
     */
    private String cron = "0 0 2 * * ?";

    /**
     * 文件保留天数
     * 默认7天
     */
    private int retentionDays = 7;
}
```

### 8. FileAutoConfig 优化

- 添加 `@EnableScheduling` 注解，支持定时任务

## 性能优化

1. **减少反射调用**: 虽然 FileResponseWrapper 仍使用反射，但添加了更好的缓存和错误处理
2. **资源管理**: 所有I/O操作都使用try-with-resources，确保资源正确释放
3. **并发处理**: FileCleanupTask 使用Stream并行处理，提高清理效率

## 可维护性改进

1. **统一异常体系**: 所有文件相关异常都继承自 FileException
2. **完善的日志**: 关键操作都有日志记录，便于问题排查
3. **清晰的职责划分**: 各组件职责明确，易于理解和维护
4. **代码注释**: 所有公共方法都有详细的JavaDoc

## 使用建议

### 1. 异常处理

```java
try{
        fileManager.validateGeneratedFile(fileId);
}catch(
FileException.FileNotFoundException e){
        // 处理文件不存在
        }catch(
FileException.FileValidationException e){
        // 处理文件验证失败
        }
```

### 2. 文件清理配置

```yaml
athena:
  file:
    path: upload
    urlPrefix: /files/
    cleanup:
      enabled: true
      retention-days: 30  # 根据业务需求调整
```

### 3. 日志级别配置

```yaml
logging:
  level:
    com.gls.athena.starter.file: DEBUG  # 开发环境
    # com.gls.athena.starter.file: INFO  # 生产环境
```

## 后续优化建议

1. **缓存优化**: 考虑对FileInfo添加缓存机制
2. **监控指标**: 添加文件操作的监控指标（如文件大小、生成时间等）
3. **批量操作**: 支持批量文件删除和清理
4. **文件压缩**: 支持大文件自动压缩
5. **安全性**: 添加文件访问权限控制

## 总结

本次优化主要关注以下几个方面：

- ✅ 异常处理更加规范和完善
- ✅ 日志记录更加详细，便于问题排查
- ✅ 代码可维护性显著提升
- ✅ 新增文件清理功能，避免磁盘空间浪费
- ✅ 性能和资源管理得到优化

所有修改都保持了向后兼容性，不会影响现有功能的使用。

