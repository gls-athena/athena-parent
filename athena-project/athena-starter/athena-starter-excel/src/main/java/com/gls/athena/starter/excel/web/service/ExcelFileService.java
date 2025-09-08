package com.gls.athena.starter.excel.web.service;

import com.gls.athena.starter.excel.web.domain.FileOutputWrapper;

import java.io.InputStream;

/**
 * Excel文件管理服务接口
 * 支持本地文件系统和OSS等多种存储方式
 *
 * @author george
 */
public interface ExcelFileService {

    /**
     * 保存文件
     *
     * @param filename    文件名
     * @param inputStream 输入流
     * @return 文件存储路径或URL
     * @throws Exception 保存失败时抛出异常
     */
    String saveFile(String filename, InputStream inputStream) throws Exception;

    /**
     * 保存文件
     *
     * @param filename 文件名
     * @param data     文件数据
     * @return 文件存储路径或URL
     * @throws Exception 保存失败时抛出异常
     */
    String saveFile(String filename, byte[] data) throws Exception;

    /**
     * 获取文件输入流
     *
     * @param filePath 文件路径或URL
     * @return 文件输入流
     * @throws Exception 获取失败时抛出异常
     */
    InputStream getFileInputStream(String filePath) throws Exception;

    /**
     * 获取文件输出流
     *
     * @param filename 文件名
     * @return 文件输出流和文件路径的包装对象
     * @throws Exception 获取失败时抛出异常
     */
    FileOutputWrapper getFileOutputStream(String filename) throws Exception;

    /**
     * 删除文件
     *
     * @param filePath 文件路径或URL
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径或URL
     * @return 是否存在
     */
    boolean fileExists(String filePath);

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径或URL
     * @return 文件大小（字节）
     */
    long getFileSize(String filePath);

    /**
     * 获取文件的下载URL（对于需要临时访问权限的存储服务）
     *
     * @param filePath      文���路径
     * @param expireSeconds 过期时间（秒）
     * @return 下载URL
     */
    String getDownloadUrl(String filePath, long expireSeconds);

}
