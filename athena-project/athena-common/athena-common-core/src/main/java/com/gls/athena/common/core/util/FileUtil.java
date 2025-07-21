package com.gls.athena.common.core.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * 文件工具类，提供静态方法来处理文件相关的操作
 *
 * @author lizy19
 */
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
        // 检查路径或文件名是否为空，如果任一为空，则返回null
        if (StrUtil.isBlank(filePath) || StrUtil.isBlank(fileName)) {
            return null;
        }
        // 如果文件名以"classpath:”开头，去除这部分，因为后面会统一通过类路径访问
        if (fileName.startsWith(CLASSPATH)) {
            fileName = fileName.substring(10);
        }
        // 如果路径以“/”开头，去除开头的“/”，确保路径是相对于类路径的
        if (filePath.startsWith(PREFIX)) {
            filePath = filePath.substring(1);
        }
        // 拼接路径和文件名，得到完整路径
        String fullPath = Paths.get(filePath, fileName).toString();
        // 使用Spring的ClassPathResource类获取完整路径对应的文件的输入流
        return new ClassPathResource(fullPath).getInputStream();
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
