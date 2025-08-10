package com.gls.athena.starter.aliyun.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Objects;

/**
 * OSS 客户端服务，封装对阿里云OSS的基础操作。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>封装OSS常用操作，如Bucket检查、文件上传、下载、删除等</li>
 *   <li>统一异常处理，便于业务层调用</li>
 *   <li>可扩展更多OSS相关操作</li>
 * </ul>
 * <p>
 * 典型用法：
 * <pre>
 * @Autowired
 * private OssClientService ossClientService;
 * boolean exists = ossClientService.doesBucketExist("my-bucket");
 * </pre>
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssClientService {

    private final OSS ossClient;

    /**
     * 检查指定的Bucket是否存在。
     * <p>
     * 内部调用OSS SDK的doesBucketExist方法，自动处理异常。
     *
     * @param bucketName bucket名称
     * @return true：存在，false：不存在或异常
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
     * @return OSS 对象，调用方需负责关闭 objectContent 流
     */
    public OSSObject getObject(String bucketName, String objectKey) {
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
            log.debug("成功获取对象: bucket={}, objectKey={}", bucketName, objectKey);
            return ossObject;
        } catch (OSSException oe) {
            log.error("OSS异常，获取对象失败: bucket={}, objectKey={}, errorCode={}, message={}",
                    bucketName, objectKey, oe.getErrorCode(), oe.getMessage());
            throw oe;
        } catch (Exception e) {
            log.error("获取对象失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new RuntimeException("获取对象失败", e);
        }
    }

    /**
     * 上传对象。
     *
     * @param bucketName  bucket 名称
     * @param objectKey   对象键
     * @param inputStream 输入流
     */
    public void putObject(String bucketName, String objectKey, InputStream inputStream) {
        Objects.requireNonNull(bucketName, "bucketName 不能为空");
        Objects.requireNonNull(objectKey, "objectKey 不能为空");
        Objects.requireNonNull(inputStream, "inputStream 不能为空");

        try {
            ossClient.putObject(bucketName, objectKey, inputStream);
            log.debug("成功上传对象: bucket={}, objectKey={}", bucketName, objectKey);
        } catch (OSSException oe) {
            log.error("OSS异常，上传对象失败: bucket={}, objectKey={}, errorCode={}, message={}",
                    bucketName, objectKey, oe.getErrorCode(), oe.getMessage());
            throw oe;
        } catch (Exception e) {
            log.error("上传对象失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new RuntimeException("上传对象失败", e);
        }
    }

    /**
     * 获取对象元数据。
     *
     * @param bucketName bucket 名称
     * @param objectKey  对象键
     * @return 对象元数据，若对象不存在则返回 null
     */
    public ObjectMetadata getObjectMetadata(String bucketName, String objectKey) {
        try {
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, objectKey);
            log.debug("成功获取对象元数据: bucket={}, objectKey={}", bucketName, objectKey);
            return metadata;
        } catch (OSSException oe) {
            if ("NoSuchKey".equals(oe.getErrorCode())) {
                log.warn("对象不存在: bucket={}, objectKey={}", bucketName, objectKey);
                return null;
            }
            log.error("OSS异常，获取对象元数据失败: bucket={}, objectKey={}, errorCode={}, message={}",
                    bucketName, objectKey, oe.getErrorCode(), oe.getMessage());
            throw oe;
        } catch (Exception e) {
            log.error("获取对象元数据失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new RuntimeException("获取对象元数据失败", e);
        }
    }
}
