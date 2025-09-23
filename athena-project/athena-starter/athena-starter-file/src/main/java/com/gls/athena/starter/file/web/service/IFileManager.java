package com.gls.athena.starter.file.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件服务接口，提供文件的基本操作功能
 *
 * @author lizy19
 */
public interface IFileManager {

    /**
     * 保存文件到指定路径
     *
     * @param path        文件存储路径
     * @param inputStream 文件输入流
     */
    void saveFile(String path, InputStream inputStream);

    /**
     * 删除指定路径的文件
     *
     * @param path 文件路径
     */
    void deleteFile(String path);

    /**
     * 检查指定路径的文件是否存在
     *
     * @param path 文件路径
     * @return 文件存在返回true，否则返回false
     */
    boolean exists(String path);

    /**
     * 获取指定路径文件的大小
     *
     * @param path 文件路径
     * @return 文件大小
     */
    long getFileSize(String path);

    /**
     * 获取指定路径文件的输入流
     *
     * @param path 文件路径
     * @return 文件输入流
     */
    InputStream getFileInputStream(String path);

    /**
     * 获取指定路径文件的输出流
     *
     * @param path 文件路径
     * @return 文件输出流
     * @throws IOException 如果无法获取输出流则抛出异常
     */
    OutputStream getFileOutputStream(String path) throws IOException;

    /**
     * 生成指定类型的文件路径
     *
     * @param type     文件类型
     * @param filename 文件名
     * @return 文件路径
     */
    String generateFilePath(String type, String filename);

    /**
     * 生成指定路径文件的访问URL
     *
     * @param path             文件路径
     * @param expiresInSeconds URL过期时间（秒）
     * @return 文件访问URL
     */
    String generateFileUrl(String path, long expiresInSeconds);
}

