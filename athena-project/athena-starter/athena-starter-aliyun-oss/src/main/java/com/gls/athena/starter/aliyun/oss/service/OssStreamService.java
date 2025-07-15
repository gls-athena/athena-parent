package com.gls.athena.starter.aliyun.oss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.ExecutorService;

/**
 * OSS 流式操作服务，封装了 OSS 对象的输入流和输出流处理。
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssStreamService {

    private final OssClientService ossClientService;
    @Qualifier("ossTaskExecutor")
    private final ExecutorService ossTaskExecutor;

    /**
     * 获取 OSS 对象的输入流。
     *
     * @param bucketName OSS 存储空间名称
     * @param objectKey  OSS 对象键（路径）
     * @return 对象输入流
     * @throws IOException 获取失败时抛出
     */
    public InputStream getInputStream(String bucketName, String objectKey) throws IOException {
        try {
            return ossClientService.getObject(bucketName, objectKey).getObjectContent();
        } catch (Exception e) {
            log.error("获取输入流失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new IOException("获取OSS对象输入流失败", e);
        }
    }

    /**
     * 获取 OSS 对象的输出流，内部通过管道流实现异步上传。
     *
     * @param bucketName OSS 存储空间名称
     * @param objectKey  OSS 对象键（路径）
     * @return 对象输出流
     * @throws IOException 创建失败时抛出
     */
    public OutputStream getOutputStream(String bucketName, String objectKey) throws IOException {
        try {
            PipedInputStream inputStream = new PipedInputStream();
            PipedOutputStream outputStream = new PipedOutputStream(inputStream);

            // 异步上传任务
            ossTaskExecutor.submit(() -> {
                try (InputStream is = inputStream) {
                    ossClientService.putObject(bucketName, objectKey, is);
                    log.debug("异步上传完成: bucket={}, objectKey={}", bucketName, objectKey);
                } catch (IOException e) {
                    log.error("异步上传失败: bucket={}, objectKey={}", bucketName, objectKey, e);
                    throw new RuntimeException("OSS文件上传失败", e);
                }
            });

            return outputStream;
        } catch (Exception e) {
            log.error("创建输出流失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new IOException("创建OSS对象输出流失败", e);
        }
    }
}
