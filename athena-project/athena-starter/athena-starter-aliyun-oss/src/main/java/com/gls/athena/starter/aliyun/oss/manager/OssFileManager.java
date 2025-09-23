package com.gls.athena.starter.aliyun.oss.manager;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.aliyun.oss.config.AliyunOssProperties;
import com.gls.athena.starter.file.manager.IFileManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * OssFileManager类实现了IFileService接口，提供基于阿里云OSS的文件管理功能
 * 包括文件的保存、删除、存在性检查、获取文件大小、生成文件路径和URL等功能
 *
 * @author george
 */
@Service
public class OssFileManager implements IFileManager {

    @Resource
    private AliyunOssProperties properties;
    @Resource
    private OssManager ossManager;

    /**
     * 将输入流保存为指定路径的文件
     *
     * @param path        文件路径
     * @param inputStream 文件输入流
     */
    @Override
    public void saveFile(String path, InputStream inputStream) {
        ossManager.putObject(properties.getBucketName(), path, inputStream);
    }

    /**
     * 删除指定路径的文件
     *
     * @param path 文件路径
     */
    @Override
    public void deleteFile(String path) {
        ossManager.deleteObject(properties.getBucketName(), path);
    }

    /**
     * 检查指定路径的文件是否存在
     *
     * @param path 文件路径
     * @return boolean 文件是否存在
     */
    @Override
    public boolean exists(String path) {
        return ossManager.doesObjectExist(properties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的大小
     *
     * @param path 文件路径
     * @return long 文件大小（字节）
     */
    @Override
    public long getFileSize(String path) {
        return ossManager.getContentLength(properties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的输入流
     *
     * @param path 文件路径
     * @return InputStream 文件输入流
     */
    @Override
    public InputStream getFileInputStream(String path) {
        return ossManager.getInputStream(properties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的输出流
     *
     * @param path 文件路径
     * @return OutputStream 文件输出流
     * @throws IOException IO异常
     */
    @Override
    public OutputStream getFileOutputStream(String path) throws IOException {
        return ossManager.getOutputStream(properties.getBucketName(), path);
    }

    /**
     * 根据文件类型和文件名生成文件路径
     * 路径格式为: [路径前缀]/[类型]/[日期]/[UUID_文件名] 或 [类型]/[日期]/[UUID_文件名]
     *
     * @param type     文件类型
     * @param filename 文件名
     * @return String 生成的文件路径
     */
    @Override
    public String generateFilePath(String type, String filename) {
        // 获取路径前缀
        String pathPrefix = properties.getPathPrefix();

        // 生成时间路径
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 生成唯一文件名
        String uuid = IdUtil.fastSimpleUUID();
        String uniqueFilename = uuid + "_" + filename;

        if (StrUtil.isNotEmpty(pathPrefix)) {
            return pathPrefix + "/" + type + "/" + datePath + "/" + uniqueFilename;
        } else {
            return type + "/" + datePath + "/" + uniqueFilename;
        }
    }

    /**
     * 生成指定路径文件的预签名URL
     *
     * @param path             文件路径
     * @param expiresInSeconds URL过期时间（秒）
     * @return String 文件的预签名URL
     */
    @Override
    public String generateFileUrl(String path, long expiresInSeconds) {
        Date expiration = new Date(System.currentTimeMillis() + expiresInSeconds * 1000);
        return ossManager.generatePresignedUrl(properties.getBucketName(), path, expiration);
    }
}
