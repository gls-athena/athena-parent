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
 * 用于监控和展示OSS客户端的配置信息和运行状态
 *
 * @author george
 */
@Endpoint(id = "oss")
public class OssEndpoint {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 获取OSS客户端配置和状态信息
     *
     * @return 返回包含以下信息的Map：
     * - beanName: Spring容器中的Bean名称
     * - endpoint: OSS服务端节点
     * - clientConfiguration: 客户端配置信息
     * - credentials: 认证信息
     * - bucketList: 当前账号下的Bucket列表
     */
    @ReadOperation
    public Map<String, Object> invoke() {
        Map<String, Object> clientsInfo = new HashMap<>();

        applicationContext.getBeansOfType(OSSClient.class)
                .forEach((beanName, ossClient) -> {
                    Map<String, Object> clientProperties = new HashMap<>();
                    clientProperties.put("beanName", beanName);
                    clientProperties.put("endpoint", ossClient.getEndpoint().toString());
                    clientProperties.put("clientConfiguration", ossClient.getClientConfiguration());
                    clientProperties.put("credentials", ossClient.getCredentialsProvider().getCredentials());
                    clientProperties.put("bucketList", ossClient.listBuckets().stream()
                            .map(Bucket::getName)
                            .toArray());

                    clientsInfo.put(beanName, clientProperties);
                });

        return clientsInfo;
    }
}
