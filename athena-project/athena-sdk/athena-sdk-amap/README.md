# 高德地图SDK配置示例

## 基础配置

```yaml
athena:
  amap:
    # 必填：高德地图API密钥
    key: your-amap-api-key

    # 可选：高德地图服务地址（默认值）
    host: https://restapi.amap.com

    # 可选：数字签名私钥（启用数字签名时需要）
    private-key: your-private-key

    # 可选：连接超时时间（毫秒，默认5000）
    connect-timeout: 5000

    # 可选：读取超时时间（毫秒，默认10000）
    read-timeout: 10000

    # 可选：重试配置
    retry:
      # 最大重试次数（默认3）
      max-attempts: 3
      # 初始重试间隔（毫秒，默认1000）
      period: 1000
      # 最大重试间隔（毫秒，默认5000）
      max-period: 5000
```

## 高级配置示例

```yaml
athena:
  amap:
    key: ${AMAP_API_KEY:your-default-key}
    host: ${AMAP_HOST:https://restapi.amap.com}
    private-key: ${AMAP_PRIVATE_KEY:}
    connect-timeout: ${AMAP_CONNECT_TIMEOUT:8000}
    read-timeout: ${AMAP_READ_TIMEOUT:15000}
    retry:
      max-attempts: ${AMAP_RETRY_MAX_ATTEMPTS:5}
      period: ${AMAP_RETRY_PERIOD:2000}
      max-period: ${AMAP_RETRY_MAX_PERIOD:10000}
```

## 使用示例

```java

@RestController
@RequiredArgsConstructor
public class LocationController {

    private final GeocodeFeign geocodeFeign;

    @GetMapping("/geocode")
    public GeoV3Response geocode(@RequestParam String address) {
        try {
            GeoV3Request request = new GeoV3Request();
            request.setAddress(address);
            return geocodeFeign.geo(request);
        } catch (AmapException e) {
            if (e.isAuthError()) {
                // 处理认证错误
                log.error("高德地图认证失败: {}", e.getErrorMessage());
            } else if (e.isQuotaError()) {
                // 处理配额限制错误
                log.warn("高德地图配额不足: {}", e.getErrorMessage());
            } else if (e.isParameterError()) {
                // 处理参数错误
                log.error("请求参数错误: {}", e.getErrorMessage());
            }
            throw e;
        }
    }
}
```

## 监控和健康检查

SDK内置了请求监控功能，会自动记录：

- API调用次数
- 调用频率
- 错误统计
- 重试情况

可以通过日志查看详细的调用信息。

## 性能优化建议

1. **连接池配置**：建议在生产环境中配置合适的连接池大小
2. **缓存策略**：对于地理编码等相对静态的数据，建议实施缓存策略
3. **批量处理**：对于大量请求，建议使用批量API接口
4. **错误处理**：合理设置重试策略，避免对高德服务器造成过大压力

## 安全建议

1. 使用环境变量或配置中心管理API密钥
2. 在生产环境中启用数字签名
3. 设置合理的IP白名单
4. 定期轮换API密钥
