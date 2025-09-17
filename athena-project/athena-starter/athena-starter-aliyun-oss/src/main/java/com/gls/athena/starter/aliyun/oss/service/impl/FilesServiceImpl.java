package com.gls.athena.starter.aliyun.oss.service.impl;

import cn.hutool.core.util.IdUtil;
import com.gls.athena.starter.aliyun.oss.config.AliyunOssProperties;
import com.gls.athena.starter.aliyun.oss.service.FilesService;
import com.gls.athena.starter.aliyun.oss.service.OssService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 文件服务实现类，提供文件的上传、删除、查询等操作接口
 *
 * @author george
 */
@Service
public class FilesServiceImpl implements FilesService {

    @Resource
    private AliyunOssProperties ossProperties;
    @Resource
    private OssService ossService;

    /**
     * 保存文件到OSS存储服务
     *
     * @param path        文件在OSS中的存储路径
     * @param inputStream 文件输入流
     */
    @Override
    public void saveFile(String path, InputStream inputStream) {
        // 将文件流上传到OSS指定的存储桶和路径
        ossService.putObject(ossProperties.getBucketName(), path, inputStream);
    }

    /**
     * 删除指定路径的文件
     *
     * @param path 文件路径
     */
    @Override
    public void deleteFile(String path) {
        ossService.deleteObject(ossProperties.getBucketName(), path);
    }

    /**
     * 检查指定路径的对象是否存在
     *
     * @param path 对象在OSS中的路径
     * @return 如果对象存在返回true，否则返回false
     */
    @Override
    public boolean exists(String path) {
        return ossService.doesObjectExist(ossProperties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的大小
     *
     * @param path 文件路径
     * @return 文件大小，以字节为单位
     */
    @Override
    public long getFileSize(String path) {
        return ossService.getContentLength(ossProperties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的输入流
     *
     * @param path 文件在OSS中的路径
     * @return 文件的输入流，用于读取文件内容
     */
    @Override
    public InputStream getFileInputStream(String path) {
        return ossService.getInputStream(ossProperties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的输出流
     *
     * @param path 文件路径
     * @return 文件输出流
     * @throws IOException IO异常
     */
    @Override
    public OutputStream getFileOutputStream(String path) throws IOException {
        return ossService.getOutputStream(ossProperties.getBucketName(), path);
    }

    /**
     * 生成文件存储路径
     *
     * @param type     文件类型，用于分类存储
     * @param filename 原始文件名
     * @return 完整的文件存储路径，格式为：路径前缀/文件类型/日期/唯一文件名
     */
    @Override
    public String generateFilePath(String type, String filename) {
        // 获取路径前缀
        String pathPrefix = ossProperties.getPathPrefix();

        // 生成时间路径
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 生成唯一文件名
        String uuid = IdUtil.fastSimpleUUID();
        String uniqueFilename = uuid + "_" + filename;

        return String.format("%s/%s/%s/%s", pathPrefix, type, datePath, uniqueFilename);
    }

    /**
     * 生成文件的预签名URL
     *
     * @param path             文件路径
     * @param expiresInSeconds URL过期时间（秒）
     * @return 预签名的文件URL
     */
    @Override
    public String generateFileUrl(String path, long expiresInSeconds) {
        // 计算URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + expiresInSeconds * 1000);
        // 生成并返回预签名URL
        return ossService.generatePresignedUrl(ossProperties.getBucketName(), path, expiration);
    }

}
