package com.gls.athena.starter.aliyun.oss.service;

import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * OSS 元数据服务，封装并简化阿里云OSS资源的元数据相关操作。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>获取对象的内容长度、元数据信息等</li>
 *   <li>统一异常处理，便于业务层调用</li>
 *   <li>可扩展更多OSS元数据相关操作</li>
 * </ul>
 * <p>
 * 典型用法：
 * <pre>
 * @Autowired
 * private OssMetadataService ossMetadataService;
 * long size = ossMetadataService.getContentLength("my-bucket", "my-object");
 * </pre>
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssMetadataService {

    private final OssClientService ossClientService;

    /**
     * 获取对象的内容长度（字节）。
     * <p>
     * 内部调用OSS元数据接口，自动处理异常并统一抛出IO异常。
     *
     * @param bucketName bucket名称
     * @param objectKey  对象键
     * @return 内容长度（字节）
     * @throws IOException 获取元数据失败时抛出
     */
    public long getContentLength(String bucketName, String objectKey) throws IOException {
        ObjectMetadata metadata = getObjectMetadataSafely(bucketName, objectKey);
        return metadata.getContentLength();
    }

    /**
     * 获取对象的最后修改时间。
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return 最后修改时间（毫秒）
     * @throws IOException 当获取元数据失败时
     */
    public long getLastModified(String bucketName, String objectKey) throws IOException {
        ObjectMetadata metadata = getObjectMetadataSafely(bucketName, objectKey);
        Date lastModified = metadata.getLastModified();
        if (lastModified == null) {
            log.warn("获取最后修改时间失败: 返回值为 null, bucket={}, objectKey={}", bucketName, objectKey);
            throw new IOException("获取OSS对象最后修改时间失败：返回值为 null");
        }
        return lastModified.getTime();
    }

    /**
     * 安全地获取对象元数据，统一异常处理。
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return ObjectMetadata 对象
     * @throws IOException 获取失败时抛出
     */
    private ObjectMetadata getObjectMetadataSafely(String bucketName, String objectKey) throws IOException {
        try {
            return ossClientService.getObjectMetadata(bucketName, objectKey);
        } catch (Exception e) {
            log.error("获取OSS对象元数据失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new IOException("获取OSS对象元数据失败", e);
        }
    }
}
