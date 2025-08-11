package com.gls.athena.starter.aliyun.oss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
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
public class OssStreamService implements DisposableBean {

    private final OssClientService ossClientService;
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
            // 调用 OSS 客户端服务获取对象内容
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
            ossTaskExecutor.submit(() -> uploadTask(inputStream, bucketName, objectKey));

            return outputStream;
        } catch (Exception e) {
            log.error("创建输出流失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new IOException("创建OSS对象输出流失败", e);
        }
    }

    /**
     * 异步上传任务。
     *
     * @param inputStream 输入流
     * @param bucketName  OSS 存储空间名称
     * @param objectKey   OSS 对象键（路径）
     */
    private void uploadTask(PipedInputStream inputStream, String bucketName, String objectKey) {
        // 执行文件上传操作
        try (InputStream is = inputStream) {
            ossClientService.putObject(bucketName, objectKey, is);
            log.debug("异步上传完成: bucket={}, objectKey={}", bucketName, objectKey);
        } catch (IOException e) {
            // 记录上传失败日志并抛出运行时异常
            log.error("异步上传失败: bucket={}, objectKey={}", bucketName, objectKey, e);
            throw new RuntimeException("OSS文件上传失败", e);
        }
    }

    /**
     * 销毁方法，在容器关闭时关闭线程池。
     * 该方法会检查线程池是否为空且未关闭，如果是则执行关闭操作。
     *
     * @throws Exception 关闭过程中可能抛出的异常
     */
    @Override
    public void destroy() throws Exception {
        // 检查线程池是否需要关闭
        if (ossTaskExecutor != null && !ossTaskExecutor.isShutdown()) {
            ossTaskExecutor.shutdown();
        }
    }

}
