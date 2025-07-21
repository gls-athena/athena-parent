package com.gls.athena.common.core.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件工具类，提供静态方法来处理文件相关的操作
 *
 * @author lizy19
 */
@Slf4j
@UtilityClass
public class FileUtil {

    public static final String CLASSPATH = "classpath:";
    public static final String PREFIX = "/";

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
        String fileFullPath = filePath.endsWith(PREFIX) ? filePath + fileName : filePath + PREFIX + fileName;
        // 如果文件名以"classpath:"开头，则从类路径获取资源
        if (fileFullPath.startsWith(CLASSPATH)) {
            fileFullPath = fileFullPath.substring(CLASSPATH.length());
        }
        if (fileFullPath.startsWith(PREFIX)) {
            fileFullPath = fileFullPath.substring(PREFIX.length());
        }
        log.debug("获取文件输入流，路径: {}", fileFullPath);
        return new ClassPathResource(fileFullPath).getInputStream();
    }

    /**
     * 提取公共逻辑：获取最后一个 '.' 的索引
     *
     * @param fileName 文件名
     * @return 最后一个 '.' 的索引，如果没有 '.' 则返回-1
     */
    private int getLastDotIndex(String fileName) {
        return fileName.lastIndexOf('.');
    }
}
