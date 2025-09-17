package com.gls.athena.starter.aliyun.oss.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件服务接口
 * 定义了文件操作的基本方法，包括文件的保存、删除、存在性检查、输入输出流获取、文件路径生成和URL生成等功能
 *
 * @author george
 */
public interface FilesService {

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
     * @throws IOException 如果无法创建输出流则抛出异常
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

