package com.gls.athena.starter.file.manager;

import cn.hutool.core.io.FileUtil;
import com.gls.athena.starter.file.config.FileProperties;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * 默认文件存储管理器实现类
 * 提供文件的保存、删除、判断存在性、获取大小、生成路径和URL等功能
 *
 * @author george
 */
@RequiredArgsConstructor
public class DefaultFileStorageManager implements IFileStorageManager {

    private final FileProperties fileProperties;

    /**
     * 将输入流保存为指定路径的文件
     *
     * @param filePath    文件保存路径
     * @param inputStream 文件输入流
     */
    @Override
    public void saveFile(String filePath, InputStream inputStream) {
        FileUtil.writeFromStream(inputStream, filePath);
    }

    /**
     * 删除指定路径的文件
     *
     * @param filePath 文件路径
     */
    @Override
    public void deleteFile(String filePath) {
        FileUtil.del(filePath);
    }

    /**
     * 判断指定路径的文件是否存在
     *
     * @param filePath 文件路径
     * @return 存在返回true，否则返回false
     */
    @Override
    public boolean exists(String filePath) {
        return FileUtil.exist(filePath);
    }

    /**
     * 获取指定路径文件的大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    @Override
    public long getFileSize(String filePath) {
        return FileUtil.file(filePath).length();
    }

    /**
     * 获取指定路径文件的输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     */
    @Override
    public InputStream getInputStream(String filePath) {
        return FileUtil.getInputStream(filePath);
    }

    /**
     * 获取指定路径文件的输出流
     *
     * @param filePath 文件路径
     * @return 文件输出流
     */
    @Override
    public OutputStream getOutputStream(String filePath) {
        return FileUtil.getOutputStream(filePath);
    }

    /**
     * 根据文件路径生成可访问的URL地址
     *
     * @param filePath    文件存储路径
     * @param expiresTime 过期时间（当前未使用）
     * @return 文件访问URL
     */
    @Override
    public String generateFileUrl(String filePath, Date expiresTime) {
        String urlPrefix = fileProperties.getUrlPrefix();
        if (urlPrefix != null) {
            return urlPrefix + "/" + filePath.replace(File.separator, "/");
        }
        return "";
    }
}
