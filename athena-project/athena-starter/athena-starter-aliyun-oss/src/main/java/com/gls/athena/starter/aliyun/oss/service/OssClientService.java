package com.gls.athena.starter.aliyun.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * OSS 客户端服务，封装对 OSS 的基本操作。
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssClientService {

    private final OSS ossClient;

    /**
     * 检查 bucket 是否存在。
     *
     * @param bucketName bucket 名称
     * @return true 如果 bucket 存在
     */
    public boolean doesBucketExist(String bucketName) {
        try {
            return ossClient.doesBucketExist(bucketName);
        } catch (Exception e) {
            log.error("检查 bucket 是否存在失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 检查对象是否存在。
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return true 如果对象存在
     */
    public boolean doesObjectExist(String bucketName, String objectKey) {
        try {
            return ossClient.doesObjectExist(bucketName, objectKey);
        } catch (Exception e) {
            log.error("检查对象是否存在失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            return false;
        }
    }

    /**
     * 获取对象。
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return OSS 对象
     */
    public OSSObject getObject(String bucketName, String objectKey) {
        return ossClient.getObject(bucketName, objectKey);
    }

    /**
     * 上传对象。
     *
     * @param bucketName  bucket 名称
     * @param objectKey   对象键
     * @param inputStream 输入流
     */
    public void putObject(String bucketName, String objectKey, InputStream inputStream) {
        ossClient.putObject(bucketName, objectKey, inputStream);
        log.debug("成功上传对象: bucket={}, objectKey={}", bucketName, objectKey);
    }

    /**
     * 获取对象元数据。
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return 对象元数据
     */
    public ObjectMetadata getObjectMetadata(String bucketName, String objectKey) {
        return ossClient.getObjectMetadata(bucketName, objectKey);
    }
}
