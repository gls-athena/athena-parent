package com.gls.athena.starter.file.manager;

import com.gls.athena.common.core.constant.FileTypeEnums;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * 文件存储管理器接口
 * 定义了文件存储相关的基本操作，包括文件URL生成、输出流获取和文件大小获取等功能
 *
 * @author george
 */
public interface IFileStorageManager {

    /**
     * 保存文件到指定路径
     *
     * @param filePath    文件路径
     * @param inputStream 文件输入流
     */
    void saveFile(String filePath, InputStream inputStream);

    /**
     * 删除指定路径的文件
     *
     * @param filePath 文件路径
     */
    void deleteFile(String filePath);

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return 文件存在返回true，否则返回false
     */
    boolean exists(String filePath);

    /**
     * 获取指定文件的大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节数）
     */
    long getFileSize(String filePath);

    /**
     * 根据文件路径获取文件输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流对象
     */
    InputStream getInputStream(String filePath);

    /**
     * 根据文件路径获取文件输出流
     *
     * @param filePath 文件路径
     * @return 文件输出流对象
     */
    OutputStream getOutputStream(String filePath);

    /**
     * 根据文件名和文件类型生成文件路径
     *
     * @param filename 文件名
     * @param type     文件类型枚举
     * @return 生成的文件路径字符串
     */
    String generateFilePath(String filename, FileTypeEnums type);

    /**
     * 根据文件路径生成文件访问URL
     *
     * @param filePath    文件路径
     * @param expiresTime 文件URL过期时间，单位毫秒
     * @return 文件访问URL字符串
     */
    String generateFileUrl(String filePath, Date expiresTime);

}

