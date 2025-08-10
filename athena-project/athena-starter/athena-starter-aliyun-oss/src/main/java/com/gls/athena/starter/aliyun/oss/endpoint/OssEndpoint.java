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
 * 阿里云OSS监控端点。
 * <p>
 * 该端点用于监控和展示OSS客户端的配置信息及运行状态，便于运维排查和系统监控。
 * <ul>
 *   <li>通过Spring应用上下文获取所有OSSClient实例</li>
 *   <li>聚合并展示每个OSS客户端的注册名称、服务端节点、Bucket信息等</li>
 *   <li>可扩展用于更多OSS运行时指标的采集</li>
 * </ul>
 * <p>
 * 典型用法：
 * <pre>
 * 通过Spring Boot Actuator访问/actuator/oss端点获取OSS相关信息
 * </pre>
 *
 * @author george
 */
@Endpoint(id = "oss")
public class OssEndpoint {

    // 定义属性键常量，提升可维护性
    private static final String BEAN_NAME = "beanName";
    private static final String ENDPOINT = "endpoint";
    private static final String CLIENT_CONFIGURATION = "clientConfiguration";
    private static final String CREDENTIALS = "credentials";
    private static final String BUCKET_LIST = "bucketList";
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 获取OSS客户端的配置信息和运行状态。
     * <p>
     * 主要功能：
     * <ul>
     *   <li>遍历Spring容器中所有OSSClient实例</li>
     *   <li>收集每个客户端的注册名称、endpoint、bucket等关键信息</li>
     *   <li>返回聚合后的OSS客户端状态信息，便于监控和排查</li>
     * </ul>
     *
     * @return Map OSS客户端配置信息和状态的聚合视图
     */
    @ReadOperation
    public Map<String, Object> invoke() {
        Map<String, Object> clientsInfo = new HashMap<>();

        // 遍历所有注册的OSSClient实例，并提取其相关信息
        applicationContext.getBeansOfType(OSSClient.class)
                .forEach((beanName, ossClient) -> {
                    // 构建单个客户端的元数据集合
                    Map<String, Object> clientProperties = new HashMap<>();
                    clientProperties.put(BEAN_NAME, beanName);
                    clientProperties.put(ENDPOINT, ossClient.getEndpoint().toString());
                    clientProperties.put(CLIENT_CONFIGURATION, ossClient.getClientConfiguration());

                    // 敏感信息脱敏处理
                    String accessKeyId = ossClient.getCredentialsProvider().getCredentials().getAccessKeyId();
                    clientProperties.put(CREDENTIALS, maskAccessKeyId(accessKeyId));

                    try {
                        // 获取并转换存储桶列表为名称数组
                        clientProperties.put(BUCKET_LIST, ossClient.listBuckets().stream()
                                .map(Bucket::getName)
                                .toArray());
                    } catch (Exception e) {
                        // 异常处理，防止一个客户端出错影响整体
                        clientProperties.put(BUCKET_LIST, new String[]{"[Failed to fetch buckets: " + e.getMessage() + "]"});
                    }

                    clientsInfo.put(beanName, clientProperties);
                });

        return clientsInfo;
    }

    /**
     * 对AccessKeyId进行脱敏处理（保留前6位，其余用*代替）
     *
     * @param accessKeyId 原始AccessKeyId
     * @return 脱敏后的字符串
     */
    private String maskAccessKeyId(String accessKeyId) {
        if (accessKeyId == null || accessKeyId.length() <= 6) {
            return "******";
        }
        return accessKeyId.substring(0, 6) + "******";
    }
}
