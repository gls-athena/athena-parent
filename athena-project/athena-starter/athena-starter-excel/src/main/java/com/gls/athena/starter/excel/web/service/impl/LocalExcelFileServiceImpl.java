package com.gls.athena.starter.excel.web.service.impl;

import com.gls.athena.starter.excel.config.ExcelProperties;
import com.gls.athena.starter.excel.web.domain.FileOutputWrapper;
import com.gls.athena.starter.excel.web.service.ExcelFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 本地文件系统的Excel文件管理服务实现
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class LocalExcelFileServiceImpl implements ExcelFileService {

    private final ExcelProperties excelProperties;

    /**
     * 保存文件到指定路径
     *
     * @param filename    文件名
     * @param inputStream 文件输入流
     * @return 保存后的文件完整路径
     * @throws Exception 保存过程中可能抛出的异常
     */
    @Override
    public String saveFile(String filename, InputStream inputStream) throws Exception {
        String filePath = generateFilePath(filename);
        createDirectoryIfNotExists(filePath);

        // 将输入流数据写入到文件
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        log.info("文件保存成功: {}", filePath);
        return filePath;
    }

    /**
     * 保存文件到指定路径
     *
     * @param filename 文件名
     * @param data     文件数据字节数组
     * @return 保存后的文件完整路径
     * @throws Exception 文件保存过程中可能出现的异常
     */
    @Override
    public String saveFile(String filename, byte[] data) throws Exception {
        // 生成文件完整路径
        String filePath = generateFilePath(filename);
        createDirectoryIfNotExists(filePath);

        // 写入文件数据
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(data);
        }

        log.info("文件保存成功: {}", filePath);
        return filePath;
    }

    /**
     * 获取指定文件的输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     * @throws Exception 当文件不存在或其他IO异常时抛出
     */
    @Override
    public InputStream getFileInputStream(String filePath) throws Exception {
        // 检查文件是否存在
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }
        // 创建并返回文件输入流
        return new FileInputStream(file);
    }

    /**
     * 获取文件输出流包装器
     *
     * @param filename 文件名
     * @return FileOutputWrapper 文件输出流包装器对象
     * @throws Exception 当文件操作出现异常时抛出
     */
    @Override
    public FileOutputWrapper getFileOutputStream(String filename) throws Exception {
        // 生成完整的文件路径
        String filePath = generateFilePath(filename);
        // 如果目录不存在则创建目录
        createDirectoryIfNotExists(filePath);

        // 创建文件输出流并包装返回
        FileOutputStream outputStream = new FileOutputStream(filePath);
        return new FileOutputWrapper()
                .setOutputStream(outputStream)
                .setFilePath(filePath);
    }

    /**
     * 删除指定路径的文件
     *
     * @param filePath 文件路径
     * @return 如果文件删除成功或文件不存在则返回true，否则返回false
     */
    @Override
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        // 检查文件是否存在
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("文件删除成功: {}", filePath);
            } else {
                log.warn("文件删除失败: {}", filePath);
            }
            return deleted;
        }
        // 文件不存在，视为删除成功
        return true;
    }

    /**
     * 检查指定路径的文件是否存在
     *
     * @param filePath 文件路径
     * @return 如果文件存在返回true，否则返回false
     */
    @Override
    public boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 获取指定文件的大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节数），如果文件不存在则返回0
     */
    @Override
    public long getFileSize(String filePath) {
        File file = new File(filePath);
        // 如果文件存在则返回文件长度，否则返回0
        return file.exists() ? file.length() : 0;
    }

    /**
     * 获取文件下载URL
     *
     * @param filePath      文件路径
     * @param expireSeconds 过期时间（秒）
     * @return 文件下载URL
     */
    @Override
    public String getDownloadUrl(String filePath, long expireSeconds) {
        // 本地文件系统不需要临时URL，直接返回文件路径
        return filePath;
    }

    /**
     * 生成文件路径
     *
     * @param filename 原始文件名
     * @return 完整文件路径
     */
    private String generateFilePath(String filename) {
        String exportDir = excelProperties.getAsyncExportDir();
        if (exportDir == null || exportDir.trim().isEmpty()) {
            exportDir = System.getProperty("java.io.tmpdir") + File.separator + "excel-exports";
        }

        // 生成带时间戳的文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        String fileExtension = getFileExtension(filename);
        String baseFilename = getBasename(filename);
        String finalFilename = String.format("%s_%s.%s", baseFilename, timestamp, fileExtension);

        return Paths.get(exportDir, finalFilename).toString();
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名，如果文件名中没有有效的扩展名则返回默认值"xlsx"
     */
    private String getFileExtension(String filename) {
        // 查找文件名中最后一个点号的位置
        int lastDotIndex = filename.lastIndexOf('.');
        // 如果点号位置有效（大于0且不是最后一个字符），则提取扩展名
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        // 如果没有找到有效的扩展名，返回默认扩展名"xlsx"
        return "xlsx";
    }

    /**
     * 获取文件基础名（不含扩展名）
     *
     * @param filename 文件名
     * @return 文件基础名，如果文件名中不包含扩展名则返回原文件名
     */
    private String getBasename(String filename) {
        // 查找最后一个点号的位置
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            // 如果找到点号且不在第一位，则截取点号前的部分作为基础名
            return filename.substring(0, lastDotIndex);
        }
        // 如果没有找到点号或点号在第一位，返回原文件名
        return filename;
    }

    /**
     * 创建目录（如果不存在）
     *
     * @param filePath 文件路径，用于获取父目录信息
     * @throws IOException 当创建目录失败时抛出
     */
    private void createDirectoryIfNotExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Path parentDir = path.getParent();
        // 检查父目录是否存在，不存在则创建
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
            log.info("创建目录: {}", parentDir);
        }
    }

}
