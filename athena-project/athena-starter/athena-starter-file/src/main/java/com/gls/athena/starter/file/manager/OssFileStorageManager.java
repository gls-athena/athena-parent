package com.gls.athena.starter.file.manager;

import com.gls.athena.starter.aliyun.oss.manager.OssFileManager;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * OSS文件存储管理器实现类
 * 该类实现了IFileStorageManager接口，提供基于阿里云OSS的文件存储管理功能
 *
 * @author george
 */
@AllArgsConstructor
public class OssFileStorageManager implements IFileStorageManager {

    private final OssFileManager ossFileManager;

    /**
     * 保存文件到OSS存储
     *
     * @param filePath    文件路径
     * @param inputStream 文件输入流
     */
    @Override
    public void saveFile(String filePath, InputStream inputStream) {
        ossFileManager.saveFile(filePath, inputStream);
    }

    /**
     * 从OSS存储中删除文件
     *
     * @param filePath 文件路径
     */
    @Override
    public void deleteFile(String filePath) {
        ossFileManager.deleteFile(filePath);
    }

    /**
     * 检查文件在OSS存储中是否存在
     *
     * @param filePath 文件路径
     * @return 文件存在返回true，否则返回false
     */
    @Override
    public boolean exists(String filePath) {
        return ossFileManager.exists(filePath);
    }

    /**
     * 获取OSS存储中文件的大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节数）
     */
    @Override
    public long getFileSize(String filePath) {
        return ossFileManager.getFileSize(filePath);
    }

    /**
     * 获取文件的输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     */
    @Override
    public InputStream getInputStream(String filePath) {
        return ossFileManager.getFileInputStream(filePath);
    }

    /**
     * 获取文件的输出流
     *
     * @param filePath 文件路径
     * @return 文件输出流
     * @throws IOException IO异常
     */
    @Override
    public OutputStream getOutputStream(String filePath) throws IOException {
        return ossFileManager.getFileOutputStream(filePath);
    }

    /**
     * 生成文件访问URL
     *
     * @param filePath    文件路径
     * @param expiresTime 过期时间
     * @return 文件访问URL
     */
    @Override
    public String generateFileUrl(String filePath, Date expiresTime) {
        return ossFileManager.generateFileUrl(filePath, expiresTime);
    }
}

