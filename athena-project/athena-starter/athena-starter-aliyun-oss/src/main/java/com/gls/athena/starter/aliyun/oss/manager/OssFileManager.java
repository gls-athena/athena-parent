package com.gls.athena.starter.aliyun.oss.manager;

import com.gls.athena.starter.aliyun.oss.config.AliyunOssProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * OssFileManager类实现了IFileService接口，提供基于阿里云OSS的文件管理功能
 * 包括文件的保存、删除、存在性检查、获取文件大小、生成文件路径和URL等功能
 *
 * @author george
 */
@Service
public class OssFileManager {

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
    public void saveFile(String path, InputStream inputStream) {
        ossManager.putObject(properties.getBucketName(), path, inputStream);
    }

    /**
     * 删除指定路径的文件
     *
     * @param path 文件路径
     */
    public void deleteFile(String path) {
        ossManager.deleteObject(properties.getBucketName(), path);
    }

    /**
     * 检查指定路径的文件是否存在
     *
     * @param path 文件路径
     * @return boolean 文件是否存在
     */
    public boolean exists(String path) {
        return ossManager.doesObjectExist(properties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的大小
     *
     * @param path 文件路径
     * @return long 文件大小（字节）
     */
    public long getFileSize(String path) {
        return ossManager.getContentLength(properties.getBucketName(), path);
    }

    /**
     * 获取指定路径文件的输入流
     *
     * @param path 文件路径
     * @return InputStream 文件输入流
     */
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
    public OutputStream getFileOutputStream(String path) throws IOException {
        return ossManager.getOutputStream(properties.getBucketName(), path);
    }

    /**
     * 生成指定路径文件的预签名URL
     *
     * @param path        文件路径
     * @param expiresTime 文件URL过期时间
     * @return String 文件的预签名URL
     */
    public String generateFileUrl(String path, Date expiresTime) {
        return ossManager.generatePresignedUrl(properties.getBucketName(), path, expiresTime);
    }
}
