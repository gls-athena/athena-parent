package com.gls.athena.starter.file.manager;

import cn.hutool.core.io.FileUtil;
import com.gls.athena.starter.file.config.FileProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
        try {
            FileUtil.writeFromStream(inputStream, filePath);
            log.debug("文件保存成功: {}", filePath);
        } catch (Exception e) {
            log.error("文件保存失败: {}", filePath, e);
            throw new RuntimeException("文件保存失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除指定路径的文件
     *
     * @param filePath 文件路径
     */
    @Override
    public void deleteFile(String filePath) {
        try {
            FileUtil.del(filePath);
            log.debug("文件删除成功: {}", filePath);
        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
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
        File file = FileUtil.file(filePath);
        return file.exists() ? file.length() : 0;
    }

    /**
     * 获取指定路径文件的输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     */
    @Override
    public InputStream getInputStream(String filePath) {
        try {
            return FileUtil.getInputStream(filePath);
        } catch (Exception e) {
            log.error("获取文件输入流失败: {}", filePath, e);
            throw new RuntimeException("获取文件输入流失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取指定路径文件的输出流
     *
     * @param filePath 文件路径
     * @return 文件输出流
     */
    @Override
    public OutputStream getOutputStream(String filePath) {
        try {
            return FileUtil.getOutputStream(filePath);
        } catch (Exception e) {
            log.error("获取文件输出流失败: {}", filePath, e);
            throw new RuntimeException("获取文件输出流失败: " + e.getMessage(), e);
        }
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
        if (urlPrefix == null || urlPrefix.isEmpty()) {
            log.warn("URL前缀未配置，返回空字符串");
            return "";
        }

        // 规范化URL路径
        String normalizedPath = filePath.replace(File.separator, "/");
        String url = urlPrefix.endsWith("/")
                ? urlPrefix + normalizedPath
                : urlPrefix + "/" + normalizedPath;

        log.debug("生成文件URL: {}", url);
        return url;
    }
}
