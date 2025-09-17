package com.gls.athena.starter.aliyun.oss.service.impl;

import com.aliyun.oss.OSS;
import com.gls.athena.starter.aliyun.oss.service.OssService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;

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
        // 提交异步任务处理文件上传
        executorService.submit(() -> {
            try (InputStream inputStream = pipedInputStream) {
                putObject(bucketName, filePath, inputStream);
            } catch (Exception e) {
                log.error("文件上传失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            }
        });
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
        // 验证存储桶名称不为null且不为空
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("Bucket name cannot be null or empty");
        }
        // 验证文件路径不为null且不为空
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
    }

    /**
     * 获取指定存储桶中文件的输入流
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return 文件的输入流，如果获取失败则返回null
     */
    @Override
    public InputStream getInputStream(String bucketName, String filePath) {
        try {
            // 验证存储桶和文件路径的有效性
            validateBucketAndPath(bucketName, filePath);
            // 从OSS获取文件对象并返回其内容流
            return ossClient.getObject(bucketName, filePath).getObjectContent();
        } catch (Exception e) {
            log.error("文件下载失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 检查指定的存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 如果存储桶存在返回true，否则返回false
     */
    @Override
    public boolean doesBucketExist(String bucketName) {
        return ossClient.doesBucketExist(bucketName);
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
        return ossClient.doesObjectExist(bucketName, filePath);
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
        // 验证存储桶名称和文件路径的有效性
        validateBucketAndPath(bucketName, filePath);
        // 通过OSS客户端获取文件元数据并返回内容长度
        return ossClient.getObjectMetadata(bucketName, filePath).getContentLength();
    }

    /**
     * 获取指定文件的最后修改时间
     *
     * @param bucketName 存储桶名称，用于定位文件所在的存储空间
     * @param filePath   文件路径，用于指定要获取修改时间的具体文件
     * @return 文件最后修改时间的时间戳（毫秒）
     * @throws IllegalArgumentException    当存储桶名称或文件路径无效时抛出
     * @throws com.aliyun.oss.OSSException 当OSS服务返回错误时抛出
     */
    @Override
    public long getLastModified(String bucketName, String filePath) {
        // 验证存储桶名称和文件路径的有效性
        validateBucketAndPath(bucketName, filePath);
        // 通过OSS客户端获取文件元数据，并返回最后修改时间
        return ossClient.getObjectMetadata(bucketName, filePath).getLastModified().getTime();
    }

    /**
     * 上传文件到OSS存储桶
     *
     * @param bucketName  存储桶名称
     * @param filePath    文件路径
     * @param inputStream 文件输入流
     */
    @Override
    public void putObject(String bucketName, String filePath, InputStream inputStream) {
        // 验证存储桶名称和文件路径的有效性
        validateBucketAndPath(bucketName, filePath);
        try {
            // 执行文件上传操作
            ossClient.putObject(bucketName, filePath, inputStream);
            log.info("文件上传成功: bucket={}, filePath={}", bucketName, filePath);
        } catch (Exception e) {
            // 记录文件上传失败的日志信息
            log.error("文件上传失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
        }
    }

    /**
     * 生成预签名URL
     * 该方法用于为指定的存储桶和文件路径生成一个带有过期时间的预签名URL，
     * 用户可以通过该URL在过期时间之前访问对应的资源
     *
     * @param bucketName 存储桶名称，不能为空
     * @param filePath   文件路径，不能为空
     * @param expiration URL过期时间，不能为空
     * @return 生成的预签名URL字符串
     */
    @Override
    public String generatePresignedUrl(String bucketName, String filePath, Date expiration) {
        // 验证存储桶名称和文件路径的有效性
        validateBucketAndPath(bucketName, filePath);
        // 调用OSS客户端生成预签名URL并返回其字符串表示
        return ossClient.generatePresignedUrl(bucketName, filePath, expiration).toString();
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
        // 验证存储桶名称和文件路径的有效性
        validateBucketAndPath(bucketName, filePath);
        try {
            // 执行文件删除操作
            ossClient.deleteObject(bucketName, filePath);
            log.info("文件删除成功: bucket={}, filePath={}", bucketName, filePath);
            return true;
        } catch (Exception e) {
            // 记录文件删除失败的日志信息
            log.error("文件删除失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            return false;
        }
    }

}

