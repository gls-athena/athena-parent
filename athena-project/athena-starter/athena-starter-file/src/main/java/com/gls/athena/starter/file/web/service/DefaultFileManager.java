package com.gls.athena.starter.file.web.service;

import com.gls.athena.starter.file.config.FileProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 默认文件管理器实现类
 * 提供本地文件系统的文件操作功能
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultFileManager implements IFileManager {

    private static final String DOT = ".";
    private static final String SLASH = "/";

    private final FileProperties fileProperties;

    /**
     * 保存文件到指定路径
     *
     * @param path        文件路径
     * @param inputStream 文件输入流
     */
    @Override
    public void saveFile(String path, InputStream inputStream) {
        try {
            Path filePath = getAbsolutePath(path);
            // 确保目录存在
            Files.createDirectories(filePath.getParent());

            // 保存文件
            try (OutputStream outputStream = Files.newOutputStream(filePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            log.info("文件保存成功: {}", path);
        } catch (IOException e) {
            log.error("文件保存失败: {}", path, e);
            throw new RuntimeException("文件保存失败", e);
        }
    }

    /**
     * 删除指定路径的文件
     *
     * @param path 文件路径
     */
    @Override
    public void deleteFile(String path) {
        try {
            Path filePath = getAbsolutePath(path);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", path);
            } else {
                log.warn("文件不存在，无法删除: {}", path);
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", path, e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     * @return 文件存在返回true，否则返回false
     */
    @Override
    public boolean exists(String path) {
        try {
            Path filePath = getAbsolutePath(path);
            return Files.exists(filePath);
        } catch (Exception e) {
            log.error("检查文件是否存在时发生错误: {}", path, e);
            return false;
        }
    }

    /**
     * 获取文件大小
     *
     * @param path 文件路径
     * @return 文件大小(字节)，文件不存在或出错时返回0
     */
    @Override
    public long getFileSize(String path) {
        try {
            Path filePath = getAbsolutePath(path);
            if (Files.exists(filePath)) {
                return Files.size(filePath);
            }
            return 0;
        } catch (IOException e) {
            log.error("获取文件大小失败: {}", path, e);
            return 0;
        }
    }

    /**
     * 获取文件输入流
     *
     * @param path 文件路径
     * @return 文件输入流，文件不存在或出错时返回null
     */
    @Override
    public InputStream getFileInputStream(String path) {
        try {
            Path filePath = getAbsolutePath(path);
            if (Files.exists(filePath)) {
                return Files.newInputStream(filePath);
            }
            return null;
        } catch (IOException e) {
            log.error("获取文件输入流失败: {}", path, e);
            throw new RuntimeException("获取文件输入流失败", e);
        }
    }

    /**
     * 获取文件输出流
     *
     * @param path 文件路径
     * @return 文件输出流
     * @throws IOException IO异常
     */
    @Override
    public OutputStream getFileOutputStream(String path) throws IOException {
        Path filePath = getAbsolutePath(path);
        // 确保目录存在
        Files.createDirectories(filePath.getParent());
        return Files.newOutputStream(filePath);
    }

    /**
     * 生成文件路径
     *
     * @param type     文件类型
     * @param filename 原始文件名
     * @return 生成的文件路径
     */
    @Override
    public String generateFilePath(String type, String filename) {
        // 生成日期目录结构：yyyy/MM/dd
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 生成唯一文件名
        String extension = "";
        if (filename != null && filename.contains(DOT)) {
            extension = filename.substring(filename.lastIndexOf(DOT));
        }
        String uniqueFilename = UUID.randomUUID() + extension;

        // 构建完整路径
        return String.format("%s/%s/%s", type, dateDir, uniqueFilename);
    }

    /**
     * 生成文件访问URL
     *
     * @param path             文件路径
     * @param expiresInSeconds 过期时间(秒)，本地存储不使用此参数
     * @return 文件访问URL
     */
    @Override
    public String generateFileUrl(String path, long expiresInSeconds) {
        // 对于本地存储，生成简单的URL（不考虑过期时间）
        String urlPrefix = fileProperties.getUrlPrefix();
        if (!urlPrefix.endsWith(SLASH)) {
            urlPrefix += SLASH;
        }
        return urlPrefix + path;
    }

    /**
     * 获取文件的绝对路径
     *
     * @param relativePath 相对路径
     * @return 绝对路径
     */
    private Path getAbsolutePath(String relativePath) {
        String basePath = fileProperties.getPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = "upload";
        }
        return Paths.get(basePath, relativePath);
    }
}
