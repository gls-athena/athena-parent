package com.gls.athena.starter.aliyun.oss.manager;

import com.aliyun.oss.OSS;
import com.gls.athena.starter.async.config.AsyncConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * OssManager接口定义了阿里云对象存储服务的基本操作
 * 提供了对OSS存储桶和对象的常用操作方法
 *
 * @author george
 */
@Slf4j
@Component
public class OssManager {
    @Resource
    private OSS ossClient;
    @Resource(name = AsyncConstants.DEFAULT_THREAD_POOL_NAME)
    private Executor executor;

    /**
     * 获取指定存储桶和文件路径的输出流
     * 通过管道流实现异步上传文件到OSS
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return OutputStream 输出流
     * @throws IOException IO异常
     */
    public OutputStream getOutputStream(String bucketName, String filePath) throws IOException {
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);

        // 使用CompletableFuture提供更好的异步处理和异常传播
        CompletableFuture.runAsync(() -> {
            try (InputStream inputStream = pipedInputStream) {
                putObject(bucketName, filePath, inputStream);
                log.debug("异步文件上传任务完成: bucket={}, filePath={}", bucketName, filePath);
            } catch (Exception e) {
                log.error("异步文件上传失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
                // 关闭输出流以通知写入端发生了错误
                try {
                    pipedOutputStream.close();
                } catch (IOException closeException) {
                    log.warn("关闭管道输出流时发生异常", closeException);
                }
            }
        }, executor);

        return pipedOutputStream;
    }

    /**
     * 获取指定存储桶和文件路径的输入流
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return InputStream 输入流
     */
    public InputStream getInputStream(String bucketName, String filePath) {
        return ossClient.getObject(bucketName, filePath).getObjectContent();
    }

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return boolean 存储桶是否存在
     */
    public boolean doesBucketExist(String bucketName) {
        return ossClient.doesBucketExist(bucketName);
    }

    /**
     * 检查对象是否存在
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return boolean 对象是否存在
     */
    public boolean doesObjectExist(String bucketName, String filePath) {
        return ossClient.doesObjectExist(bucketName, filePath);
    }

    /**
     * 获取对象的内容长度
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return long 对象内容长度
     */
    public long getContentLength(String bucketName, String filePath) {
        return ossClient.getObjectMetadata(bucketName, filePath).getContentLength();
    }

    /**
     * 获取对象的最后修改时间
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return long 对象最后修改时间的时间戳
     */
    public long getLastModified(String bucketName, String filePath) {
        return ossClient.getObjectMetadata(bucketName, filePath).getLastModified().getTime();
    }

    /**
     * 上传对象到指定存储桶
     *
     * @param bucketName  存储桶名称
     * @param filePath    文件路径
     * @param inputStream 输入流
     */
    public void putObject(String bucketName, String filePath, InputStream inputStream) {
        ossClient.putObject(bucketName, filePath, inputStream);
    }

    /**
     * 生成预签名URL
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @param expiration 过期时间
     * @return String 预签名URL字符串
     */
    public String generatePresignedUrl(String bucketName, String filePath, Date expiration) {
        return ossClient.generatePresignedUrl(bucketName, filePath, expiration).toString();
    }

    /**
     * 删除指定对象
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     */
    public void deleteObject(String bucketName, String filePath) {
        ossClient.deleteObject(bucketName, filePath);
    }
}
