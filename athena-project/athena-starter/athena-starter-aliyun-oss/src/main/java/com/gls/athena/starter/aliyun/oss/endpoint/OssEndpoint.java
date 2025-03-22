package com.gls.athena.starter.aliyun.oss.endpoint;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import jakarta.annotation.Resource;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云OSS监控端点
 * <p>
 * 用于监控和展示OSS客户端的配置信息和运行状态。
 *
 * @author george
 */
@Endpoint(id = "oss")
public class OssEndpoint {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 获取OSS客户端配置和状态信息。
     * <p>
     * 通过Spring应用上下文获取所有OSSClient实例，并聚合以下信息：
     * - 客户端在Spring容器中的注册名称
     * - 服务端节点地址
     * - 完整的客户端配置对象
     * - 身份凭证信息
     * - 当前账户下所有存储桶的名称列表
     *
     * @return 返回嵌套的字典结构，外层字典的key为bean名称，value为对应客户端的详细信息字典。
     */
    @ReadOperation
    public Map<String, Object> invoke() {
        Map<String, Object> clientsInfo = new HashMap<>();

        // 遍历所有注册的OSSClient实例
        applicationContext.getBeansOfType(OSSClient.class)
                .forEach((beanName, ossClient) -> {
                    // 构建单个客户端的元数据集合
                    Map<String, Object> clientProperties = new HashMap<>();
                    clientProperties.put("beanName", beanName);
                    clientProperties.put("endpoint", ossClient.getEndpoint().toString());
                    clientProperties.put("clientConfiguration", ossClient.getClientConfiguration());
                    clientProperties.put("credentials", ossClient.getCredentialsProvider().getCredentials());

                    // 获取并转换存储桶列表为名称数组
                    clientProperties.put("bucketList", ossClient.listBuckets().stream()
                            .map(Bucket::getName)
                            .toArray());

                    clientsInfo.put(beanName, clientProperties);
                });

        return clientsInfo;
    }
}
