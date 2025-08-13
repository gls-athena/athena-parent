package com.gls.athena.common.core.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 文件工具类，提供静态方法来处理文件相关的操作
 *
 * @author george
 */
@Slf4j
@UtilityClass
public class FileUtil {

    public static final String CLASSPATH = "classpath:";
    public static final String PREFIX = "/";

    // 常见的图片文件扩展名
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico"
    );

    // 常见的文档文件扩展名
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf"
    );

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名，如果没有扩展名则返回空字符串
     */
    public String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        int lastIndexOfDot = getLastDotIndex(fileName);
        if (lastIndexOfDot == -1 || lastIndexOfDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    /**
     * 获取文件名，不包含扩展名
     *
     * @param fileName 文件名
     * @return 不包含扩展名的文件名，如果没有扩展名则返回完整文件名
     */
    public String getFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        int lastIndexOfDot = getLastDotIndex(fileName);
        if (lastIndexOfDot == -1 || lastIndexOfDot == fileName.length() - 1) {
            return fileName;
        }
        return fileName.substring(0, lastIndexOfDot);
    }

    /**
     * 获取指定文件的输入流
     * 该方法用于根据文件路径和文件名，从类路径下获取文件的输入流
     *
     * @param filePath 文件所在的目录路径，不应以“/”开头
     * @param fileName 文件名，如果以"classpath:"开头，则表示从类路径获取
     * @return InputStream 返回文件的输入流，如果路径或文件名为空，则返回null
     * @throws IOException 当文件无法访问或读取时抛出此异常
     */
    public InputStream getInputStream(String filePath, String fileName) throws IOException {
        if (StrUtil.isBlank(filePath) || StrUtil.isBlank(fileName)) {
            return null;
        }

        String fileFullPath = buildFilePath(filePath, fileName);
        log.debug("获取文件输入流，路径: {}", fileFullPath);

        // 去除 classpath 前缀
        if (fileFullPath.startsWith(CLASSPATH)) {
            fileFullPath = fileFullPath.substring(CLASSPATH.length());
        }

        // 去除前导斜杠
        if (fileFullPath.startsWith(PREFIX)) {
            fileFullPath = fileFullPath.substring(PREFIX.length());
        }

        return new ClassPathResource(fileFullPath).getInputStream();
    }

    /**
     * 构建文件完整路径
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 完整路径
     */
    private String buildFilePath(String filePath, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(filePath);
        if (!filePath.endsWith(PREFIX)) {
            sb.append(PREFIX);
        }
        sb.append(fileName);
        return sb.toString();
    }

    /**
     * 获取文件最后一个点的索引位置
     *
     * @param fileName 文件名
     * @return 最后一个点的索引，如果没有找到则返回-1
     */
    private int getLastDotIndex(String fileName) {
        return fileName.lastIndexOf('.');
    }

    /**
     * 验证文件扩展名是否安全
     *
     * @param fileName          文件名
     * @param allowedExtensions 允许的扩展名集合
     * @return 如果扩展名在允许列表中返回true，否则返回false
     */
    public boolean isAllowedExtension(String fileName, Set<String> allowedExtensions) {
        if (!StringUtils.hasText(fileName) || allowedExtensions == null || allowedExtensions.isEmpty()) {
            return false;
        }
        String extension = getFileExtension(fileName).toLowerCase();
        return allowedExtensions.contains(extension);
    }

    /**
     * 判断是否为图片文件
     *
     * @param fileName 文件名
     * @return 如果是图片文件返回true，否则返回false
     */
    public boolean isImageFile(String fileName) {
        return isAllowedExtension(fileName, IMAGE_EXTENSIONS);
    }

    /**
     * 判断是否为文档文件
     *
     * @param fileName 文件名
     * @return 如果是文档文件返回true，否则返回false
     */
    public boolean isDocumentFile(String fileName) {
        return isAllowedExtension(fileName, DOCUMENT_EXTENSIONS);
    }

    /**
     * 格式化文件大小
     *
     * @param bytes 文件大小（字节）
     * @return 格式化后的文件大小字符串
     */
    public String formatFileSize(long bytes) {
        if (bytes < 0) {
            return "0 B";
        }

        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * 安全的文件名处理，移除潜在的危险字符
     *
     * @param fileName 原始文件名
     * @return 安全的文件名
     */
    public String sanitizeFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "unnamed";
        }

        // 移除路径分隔符和其他危险字符
        String sanitized = fileName.replaceAll("[/\\\\:*?\"<>|]", "_");

        // 移除连续的点号（防止目录遍历）
        sanitized = sanitized.replaceAll("\\.{2,}", ".");

        // 确保不以点号开头或结尾
        sanitized = sanitized.replaceAll("^\\.|\\.$", "");

        // 如果处理后为空，则使用默认名称
        if (!StringUtils.hasText(sanitized)) {
            sanitized = "unnamed";
        }

        return sanitized;
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 如果文件存在返回true，否则返回false
     */
    public boolean exists(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            return Files.exists(path);
        } catch (Exception e) {
            log.warn("检查文件是否存在时发生异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建目录（如果不存在）
     *
     * @param dirPath 目录路径
     * @return 创建成功返回true，否则返回false
     */
    public boolean createDirectories(String dirPath) {
        if (!StringUtils.hasText(dirPath)) {
            return false;
        }

        try {
            Path path = Paths.get(dirPath);
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            log.error("创建目录失败: {}, 错误: {}", dirPath, e.getMessage());
            return false;
        }
    }
}
