package com.gls.athena.starter.aliyun.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.gls.athena.starter.aliyun.oss.service.OssService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * OSS服务实现类
 *
 * @author george
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {
    @Resource
    private OSS ossClient;
    @Resource
    private ExecutorService executorService;

    /**
     * 获取指定存储桶和文件路径的输出流，用于上传文件
     *
     * @param bucketName 存储桶名称，不能为空
     * @param filePath   文件路径，不能为空
     * @return OutputStream 文件输出流，用于写入文件内容
     * @throws IOException 当IO操作失败时抛出
     */
    @Override
    public OutputStream getOutputStream(String bucketName, String filePath) throws IOException {
        validateBucketAndPath(bucketName, filePath);

        // 创建管道输入流和输出流，用于在不同线程间传输数据
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
        }, executorService);

        return pipedOutputStream;
    }

    /**
     * 验证存储桶名称和文件路径的有效性
     *
     * @param bucketName 存储桶名称，不能为空
     * @param filePath   文件路径，不能为空
     * @throws IllegalArgumentException 当存储桶名称或文件路径为null或空字符串时抛出
     */
    private void validateBucketAndPath(String bucketName, String filePath) {
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalArgumentException("存储桶名称不能为null或空字符串");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为null或空字符串");
        }
    }

    /**
     * 验证过期时间参数
     *
     * @param expiration 过期时间
     * @throws IllegalArgumentException 当过期时间为null或已过期时抛出
     */
    private void validateExpiration(Date expiration) {
        if (expiration == null) {
            throw new IllegalArgumentException("过期时间不能为null");
        }
        if (expiration.before(new Date())) {
            throw new IllegalArgumentException("过期时间不能早于当前时间");
        }
    }

    /**
     * 通用的OSS操作异常处理
     *
     * @param operation  操作描述
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @param supplier   具体的OSS操作
     * @param <T>        返回类型
     * @return 操作结果
     */
    private <T> T executeOssOperation(String operation, String bucketName, String filePath, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (OSSException e) {
            log.error("OSS{}操作失败: bucket={}, filePath={}, errorCode={}, errorMessage={}",
                    operation, bucketName, filePath, e.getErrorCode(), e.getErrorMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("{}操作发生未知异常: bucket={}, filePath={}, error={}",
                    operation, bucketName, filePath, e.getMessage(), e);
            throw new RuntimeException("OSS操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取指定存储桶中文件的输入流
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return 文件的输入流，如果获取失败则抛出异常
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException         当OSS操作失败时抛出
     */
    @Override
    public InputStream getInputStream(String bucketName, String filePath) {
        validateBucketAndPath(bucketName, filePath);
        return executeOssOperation("文件下载", bucketName, filePath,
                () -> ossClient.getObject(bucketName, filePath).getObjectContent());
    }

    /**
     * 检查指定的存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 如果存储桶存在返回true，否则返回false
     * @throws IllegalArgumentException 当存储桶名称无效时抛出
     */
    @Override
    public boolean doesBucketExist(String bucketName) {
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalArgumentException("存储桶名称不能为null或空字符串");
        }
        return executeOssOperation("存储桶存在性检查", bucketName, null,
                () -> ossClient.doesBucketExist(bucketName));
    }

    /**
     * 检查指定存储桶中的对象是否存在
     *
     * @param bucketName 存储桶名称，不能为空
     * @param filePath   对象文件路径，不能为空
     * @return 如果对象存在返回true，否则返回false
     */
    @Override
    public boolean doesObjectExist(String bucketName, String filePath) {
        validateBucketAndPath(bucketName, filePath);
        return executeOssOperation("对象存在性检查", bucketName, filePath,
                () -> ossClient.doesObjectExist(bucketName, filePath));
    }

    /**
     * 获取指定文件的内容长度
     *
     * @param bucketName 存储桶名称，用于定位文件所在的存储空间
     * @param filePath   文件路径，用于指定要获取长度的具体文件
     * @return 文件的内容长度，以字节为单位
     */
    @Override
    public long getContentLength(String bucketName, String filePath) {
        validateBucketAndPath(bucketName, filePath);
        return executeOssOperation("获取文件内容长度", bucketName, filePath,
                () -> ossClient.getObjectMetadata(bucketName, filePath).getContentLength());
    }

    /**
     * 获取指定文件的最后修改时间
     *
     * @param bucketName 存储桶名称，用于定位文件所在的存储空间
     * @param filePath   文件路径，用于指定要获取修改时间的具体文件
     * @return 文件最后修改时间的时间戳（毫秒）
     * @throws IllegalArgumentException 当存储桶名称或文件路径无效时抛出
     * @throws RuntimeException         当OSS服务返回错误时抛出
     */
    @Override
    public long getLastModified(String bucketName, String filePath) {
        validateBucketAndPath(bucketName, filePath);
        return executeOssOperation("获取文件最后修改时间", bucketName, filePath,
                () -> ossClient.getObjectMetadata(bucketName, filePath).getLastModified().getTime());
    }

    /**
     * 上传文件到OSS存储桶
     *
     * @param bucketName  存储桶名称
     * @param filePath    文件路径
     * @param inputStream 文件输入流
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException         当上传失败时抛出
     */
    @Override
    public void putObject(String bucketName, String filePath, InputStream inputStream) {
        validateBucketAndPath(bucketName, filePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为null");
        }

        executeOssOperation("文件上传", bucketName, filePath, () -> {
            ossClient.putObject(bucketName, filePath, inputStream);
            log.info("文件上传成功: bucket={}, filePath={}", bucketName, filePath);
            return null;
        });
    }

    /**
     * 生成预签名URL
     * 该方法用于为指定的存储桶和文件路径生成一个带有过期时间的预签名URL，
     * 用户可以通过该URL在过期时间之前访问对应的资源
     *
     * @param bucketName 存储桶名称，不能为空
     * @param filePath   文件路径，不能为空
     * @param expiration URL过期时间，不能为空且不能早于当前时间
     * @return 生成的预签名URL字符串
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException         当生成URL��败时抛出
     */
    @Override
    public String generatePresignedUrl(String bucketName, String filePath, Date expiration) {
        validateBucketAndPath(bucketName, filePath);
        validateExpiration(expiration);

        return executeOssOperation("生成预签名URL", bucketName, filePath,
                () -> ossClient.generatePresignedUrl(bucketName, filePath, expiration).toString());
    }

    /**
     * 删除指定存储桶中的文件对象
     *
     * @param bucketName 存储桶名称，不能为空
     * @param filePath   文件路径，不能为空
     * @return 删除成功返回true，删除失败返回false
     */
    @Override
    public boolean deleteObject(String bucketName, String filePath) {
        validateBucketAndPath(bucketName, filePath);
        try {
            executeOssOperation("文件删除", bucketName, filePath, () -> {
                ossClient.deleteObject(bucketName, filePath);
                return null;
            });
            log.info("文件删除成功: bucket={}, filePath={}", bucketName, filePath);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            return false;
        }
    }
}
