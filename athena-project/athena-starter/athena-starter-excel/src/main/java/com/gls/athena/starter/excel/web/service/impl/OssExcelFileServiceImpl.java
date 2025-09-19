package com.gls.athena.starter.excel.web.service.impl;

import com.aliyun.oss.OSS;
import com.gls.athena.starter.aliyun.oss.manager.FileManager;
import com.gls.athena.starter.excel.web.domain.FileOutputWrapper;
import com.gls.athena.starter.excel.web.service.ExcelFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 阿里云OSS的Excel文件管理服务实现
 *
 * @author george
 */
@Slf4j
@Service
@ConditionalOnClass({OSS.class})
@ConditionalOnProperty(prefix = "athena.excel.file", name = "type", havingValue = "oss", matchIfMissing = true)
public class OssExcelFileServiceImpl implements ExcelFileService {
    @Resource
    private FileManager fileManager;

    /**
     * 将输入流保存为指定名称的文件
     *
     * @param filename    文件名
     * @param inputStream 文件输入流
     * @return 保存后的文件路径
     * @throws Exception 保存过程中可能抛出的异常
     */
    @Override
    public String saveFile(String filename, InputStream inputStream) throws Exception {
        String path = fileManager.generateFilePath("excel", filename);
        fileManager.saveFile(path, inputStream);
        return path;
    }

    /**
     * 将字节数组保存为指定名称的文件
     *
     * @param filename 文件名
     * @param data     文件数据（字节数组）
     * @return 保存后的文件路径
     * @throws Exception 保存过程中可能抛出的异常
     */
    @Override
    public String saveFile(String filename, byte[] data) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(data);
        return saveFile(filename, inputStream);
    }

    /**
     * 获取指定文件路径的输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     * @throws Exception 获取输入流过程中可能抛出的异常
     */
    @Override
    public InputStream getFileInputStream(String filePath) throws Exception {
        return fileManager.getFileInputStream(filePath);
    }

    /**
     * 获取用于写入文件的输出流包装器
     *
     * @param filename 文件名
     * @return 包含输出流和文件路径的包装对象
     * @throws Exception 创建输出流过程中可能抛出的异常
     */
    @Override
    public FileOutputWrapper getFileOutputStream(String filename) throws Exception {
        String path = fileManager.generateFilePath("excel", filename);
        OutputStream outputStream = fileManager.getFileOutputStream(path);
        return new FileOutputWrapper()
                .setOutputStream(outputStream)
                .setFilePath(path);
    }

    /**
     * 删除指定路径的文件
     *
     * @param filePath 文件路径
     * @return 删除是否成功
     */
    @Override
    public boolean deleteFile(String filePath) {
        try {
            fileManager.deleteFile(filePath);
            return true;
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return false;
        }
    }

    /**
     * 判断指定路径的文件是否存在
     *
     * @param filePath 文件路径
     * @return 文件是否存在
     */
    @Override
    public boolean fileExists(String filePath) {
        return fileManager.exists(filePath);
    }

    /**
     * 获取指定文件的大小
     *
     * @param filePath 文件路径
     * @return 文件大小（单位：字节）
     */
    @Override
    public long getFileSize(String filePath) {
        return fileManager.getFileSize(filePath);
    }

    /**
     * 生成文件的下载URL
     *
     * @param filePath      文件路径
     * @param expireSeconds URL过期时间（秒）
     * @return 文件下载URL
     */
    @Override
    public String getDownloadUrl(String filePath, long expireSeconds) {
        return fileManager.generateFileUrl(filePath, expireSeconds);
    }
}
