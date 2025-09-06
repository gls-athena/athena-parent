package com.gls.athena.starter.excel.web.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.gls.athena.starter.aliyun.oss.config.AliyunOssProperties;
import com.gls.athena.starter.aliyun.oss.service.OssClientService;
import com.gls.athena.starter.aliyun.oss.service.OssMetadataService;
import com.gls.athena.starter.aliyun.oss.service.OssStreamService;
import com.gls.athena.starter.excel.web.domain.FileOutputWrapper;
import com.gls.athena.starter.excel.web.service.ExcelFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS的Excel文件管理服务实现
 *
 * @author george
 */
@Slf4j
@Service
@ConditionalOnClass({OSS.class, OssClientService.class})
public class OssExcelFileServiceImpl implements ExcelFileService {
    @Resource
    private OssClientService ossClientService;
    @Resource
    private OssStreamService ossStreamService;
    @Resource
    private OssMetadataService ossMetadataService;
    @Resource
    private AliyunOssProperties ossProperties;
    @Resource
    private OSS ossClient;

    /**
     * 通过输入流保存文件到OSS
     *
     * @param filename    文件名
     * @param inputStream 文件输入流
     * @return 文件在OSS中的路径或标识符
     * @throws Exception 保存过程中可能抛出的异常
     */
    @Override
    public String saveFile(String filename, InputStream inputStream) throws Exception {
        String filePath = generateFilePath(filename);
        String bucketName = ossProperties.getBucketName();

        try {
            ossClientService.putObject(bucketName, filePath, inputStream);
            log.info("文件上传成功: bucket={}, filePath={}", bucketName, filePath);
            return filePath;
        } catch (Exception e) {
            log.error("文件上传失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            throw new Exception("文件上传到OSS失败", e);
        }
    }

    /**
     * 通过字节数组保存文件到OSS
     *
     * @param filename 文件名
     * @param data     文件数据字节数组
     * @return 文件在OSS中的路径或标识符
     * @throws Exception 保存过程中可能抛出的异常
     */
    @Override
    public String saveFile(String filename, byte[] data) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            return saveFile(filename, inputStream);
        }
    }

    /**
     * 获取指定文件的输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     * @throws Exception 获取过程中可能抛出的异常
     */
    @Override
    public InputStream getFileInputStream(String filePath) throws Exception {
        String bucketName = ossProperties.getBucketName();

        try {
            if (!ossClientService.doesObjectExist(bucketName, filePath)) {
                throw new Exception("文件不存在: " + filePath);
            }

            InputStream inputStream = ossStreamService.getInputStream(bucketName, filePath);
            log.debug("获取文件输入流成功: bucket={}, filePath={}", bucketName, filePath);
            return inputStream;
        } catch (Exception e) {
            log.error("获取文件输入流失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            throw new Exception("获取文件输入流失败", e);
        }
    }

    /**
     * 获取用于写入文件的输出流包装器
     *
     * @param filename 文件名
     * @return 文件输出流包装器
     * @throws Exception 创建输出流过程中可能抛出的异常
     */
    @Override
    public FileOutputWrapper getFileOutputStream(String filename) throws Exception {
        String filePath = generateFilePath(filename);
        String bucketName = ossProperties.getBucketName();

        try {
            OutputStream outputStream = ossStreamService.getOutputStream(bucketName, filePath);

            FileOutputWrapper wrapper = new FileOutputWrapper()
                    .setOutputStream(outputStream)
                    .setFilePath(filePath);

            log.debug("创建文件输出流成功: bucket={}, filePath={}", bucketName, filePath);
            return wrapper;
        } catch (Exception e) {
            log.error("创建文件输出流失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            throw new Exception("创建文件输出流失败", e);
        }
    }

    /**
     * 删除指定路径的文件
     *
     * @param filePath 文件路径
     * @return 删除是否成功
     */
    @Override
    public boolean deleteFile(String filePath) {
        String bucketName = ossProperties.getBucketName();

        try {
            if (!ossClientService.doesObjectExist(bucketName, filePath)) {
                log.warn("要删除的文件不存在: bucket={}, filePath={}", bucketName, filePath);
                return false;
            }

            ossClient.deleteObject(bucketName, filePath);
            log.info("文件删除成功: bucket={}, filePath={}", bucketName, filePath);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查指定路径的文件是否存在
     *
     * @param filePath 文件路径
     * @return 文件是否存在
     */
    @Override
    public boolean fileExists(String filePath) {
        String bucketName = ossProperties.getBucketName();

        try {
            boolean exists = ossClientService.doesObjectExist(bucketName, filePath);
            log.debug("检查文件存在性: bucket={}, filePath={}, exists={}", bucketName, filePath, exists);
            return exists;
        } catch (Exception e) {
            log.error("检查文件存在性失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取指定文件的大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    @Override
    public long getFileSize(String filePath) {
        String bucketName = ossProperties.getBucketName();

        try {
            if (!ossClientService.doesObjectExist(bucketName, filePath)) {
                log.warn("文件不存在，无法获取大小: bucket={}, filePath={}", bucketName, filePath);
                return 0;
            }

            long size = ossMetadataService.getContentLength(bucketName, filePath);
            log.debug("获取文件大小成功: bucket={}, filePath={}, size={}", bucketName, filePath, size);
            return size;
        } catch (Exception e) {
            log.error("获取文件大小失败: bucket={}, filePath={}, error={}", bucketName, filePath, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取文件的下载URL
     *
     * @param filePath      文件路径
     * @param expireSeconds URL过期时间（秒）
     * @return 文件下载URL
     */
    @Override
    public String getDownloadUrl(String filePath, long expireSeconds) {
        String bucketName = ossProperties.getBucketName();

        try {
            if (!ossClientService.doesObjectExist(bucketName, filePath)) {
                log.warn("文件不存在，无法生成下载URL: bucket={}, filePath={}", bucketName, filePath);
                return null;
            }

            // 计算过期时间
            Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000);

            // 生成预签名URL请求
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filePath);
            request.setExpiration(expiration);

            // 生成预签名URL
            URL url = ossClient.generatePresignedUrl(request);
            String downloadUrl = url.toString();

            log.debug("生成下载URL成功: bucket={}, filePath={}, expireSeconds={}, url={}",
                    bucketName, filePath, expireSeconds, downloadUrl);
            return downloadUrl;
        } catch (Exception e) {
            log.error("生成下载URL失败: bucket={}, filePath={}, expireSeconds={}, error={}",
                    bucketName, filePath, expireSeconds, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成文件路径
     * 路径格式: pathPrefix/excel/yyyy/MM/dd/uuid_filename
     *
     * @param filename 原始文件名
     * @return 生成的文件路径
     */
    private String generateFilePath(String filename) {
        // 获取路径前缀
        String pathPrefix = ossProperties.getPathPrefix();

        // 生成时间路径
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String uniqueFilename = uuid + "_" + filename;

        // 组合完整路径
        StringBuilder pathBuilder = new StringBuilder();
        if (StringUtils.hasText(pathPrefix)) {
            pathBuilder.append(pathPrefix);
            if (!pathPrefix.endsWith("/")) {
                pathBuilder.append("/");
            }
        }
        pathBuilder.append("excel/");
        pathBuilder.append(datePath);
        pathBuilder.append("/");
        pathBuilder.append(uniqueFilename);

        return pathBuilder.toString();
    }
}
