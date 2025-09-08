package com.gls.athena.starter.aliyun.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
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
        // 调用OSS客户端检查bucket是否存在，捕获所有异常并记录错误日志
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
            // 调用OSS客户端检查对象是否存在
            return ossClient.doesObjectExist(bucketName, objectKey);
        } catch (Exception e) {
            // 记录检查对象存在性时发生的异常，并返回false表示对象不存在
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
            // 调用OSS客户端获取对象
            OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
            log.debug("成功获取对象: bucket={}, objectKey={}", bucketName, objectKey);
            return ossObject;
        } catch (OSSException oe) {
            // 处理OSS异常情况
            log.error("OSS异常，获取对象失败: bucket={}, objectKey={}, errorCode={}, message={}",
                    bucketName, objectKey, oe.getErrorCode(), oe.getMessage());
            throw oe;
        } catch (Exception e) {
            // 处理其他异常情况
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
        // 参数校验，确保必要参数不为null
        Objects.requireNonNull(bucketName, "bucketName 不能为空");
        Objects.requireNonNull(objectKey, "objectKey 不能为空");
        Objects.requireNonNull(inputStream, "inputStream 不能为空");

        try {
            // 调用OSS客户端上传对象
            ossClient.putObject(bucketName, objectKey, inputStream);
            log.debug("成功上传对象: bucket={}, objectKey={}", bucketName, objectKey);
        } catch (OSSException oe) {
            // 处理OSS异常，记录错误日志并重新抛出
            log.error("OSS异常，上传对象失败: bucket={}, objectKey={}, errorCode={}, message={}",
                    bucketName, objectKey, oe.getErrorCode(), oe.getMessage());
            throw oe;
        } catch (Exception e) {
            // 处理其他异常，记录错误日志并封装为运行时异常抛出
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
            // 调用OSS客户端获取对象元数据
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, objectKey);
            log.debug("成功获取对象元数据: bucket={}, objectKey={}", bucketName, objectKey);
            return metadata;
        } catch (OSSException oe) {
            // 处理OSS异常情况
            if ("NoSuchKey".equals(oe.getErrorCode())) {
                log.warn("对象不存在: bucket={}, objectKey={}", bucketName, objectKey);
                return null;
            }
            log.error("OSS异常，获取对象元数据失败: bucket={}, objectKey={}, errorCode={}, message={}",
                    bucketName, objectKey, oe.getErrorCode(), oe.getMessage());
            throw oe;
        } catch (Exception e) {
            // 处理其他异常情况
            log.error("获取对象元数据失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new RuntimeException("获取对象元数据失败", e);
        }
    }

    /**
     * 删除OSS存储桶中的对象
     *
     * @param bucketName 存储桶名称
     * @param filePath   对象文件路径
     * @return 删除成功返回true，删除失败返回false
     */
    public boolean deleteObject(String bucketName, String filePath) {
        try {
            // 执行删除操作
            ossClient.deleteObject(bucketName, filePath);
            log.debug("成功删除对象: bucket={}, filePath={}", bucketName, filePath);
            return true;
        } catch (Exception e) {
            // 记录删除失败的日志信息
            log.error("删除对象失败: bucket={}, filePath={}", bucketName, filePath, e);
            return false;
        }
    }

    /**
     * 生成预签名URL
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @param expiration 过期时间
     * @return 预签名URL字符串，生成失败时返回null
     */
    public String generatePresignedUrl(String bucketName, String filePath, Date expiration) {
        try {
            // 调用OSSClient生成预签名URL
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filePath);
            request.setExpiration(expiration);
            URL url = ossClient.generatePresignedUrl(request);

            if (url != null) {
                log.debug("成功生成预签名URL: bucket={}, filePath={}, expiration={}, url={}",
                        bucketName, filePath, expiration, url);
                return url.toString();
            } else {
                log.error("生成预签名URL失败，返回null: bucket={}, filePath={}, expiration={}",
                        bucketName, filePath, expiration);
                return null;
            }
        } catch (Exception e) {
            log.error("生成预签名URL失败: bucket={}, filePath={}, expiration={}, error={}",
                    bucketName, filePath, expiration, e.getMessage(), e);
            return null;
        }
    }

}

