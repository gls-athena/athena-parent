package com.gls.athena.starter.file.core.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.gls.athena.common.core.enums.FileTypeEnums;
import com.gls.athena.starter.file.core.config.FileProperties;
import com.gls.athena.starter.file.core.domain.FileInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 文件服务接口，提供文件的基本操作功能
 *
 * @author george
 */
@Slf4j
@Component
public class FileManager {
    @Resource
    private IFileInfoManager fileInfoManager;
    @Resource
    private IFileStorageManager fileStorageManager;
    @Resource
    private FileProperties fileProperties;

    /**
     * 生成文件信息并保存
     *
     * @param type     文件类型枚举，用于确定文件分类及存储目录
     * @param filename 原始文件名称，用于构建最终存储文件名
     * @return 生成的文件信息对象，包含文件ID、路径、URL等元数据
     */
    public FileInfo generateFileInfo(FileTypeEnums type, String filename) {
        // 生成唯一文件ID
        String fileId = IdUtil.fastSimpleUUID();
        // 生成文件存储路径
        String filePath = generateFilePath(filename, type);
        // 计算文件访问URL的过期时间（当前时间+1小时）
        Date expireTime = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        // 生成临时可访问的文件URL
        String fileUrl = fileStorageManager.generateFileUrl(filePath, expireTime);
        // 构建文件信息对象
        FileInfo fileInfo = new FileInfo()
                .setFileId(fileId)
                .setFileName(filename)
                .setFilePath(filePath)
                .setFileType(type)
                .setFileSize(0)
                .setFileUrl(fileUrl)
                .setFileUrlExpireTime(expireTime);

        try {
            // 保存文件信息到数据库
            fileInfoManager.saveFileInfo(fileInfo);
            log.debug("文件信息已生成并保存: fileId={}, filename={}", fileId, filename);
        } catch (Exception e) {
            log.error("保存文件信息失败: fileId={}, filename={}", fileId, filename, e);
            throw new RuntimeException("保存文件信息失败: " + e.getMessage(), e);
        }

        return fileInfo;
    }

    /**
     * 根据原始文件名和类型生成实际存储路径
     * 格式：{basePath}/{type}/{yyyy-MM-dd}/{uuid}_{filename}.{extension}
     *
     * @param filename 原始文件名
     * @param type     文件类型枚举，决定子目录结构
     * @return 完整的标准化文件存储路径
     */
    private String generateFilePath(String filename, FileTypeEnums type) {
        String basePath = fileProperties.getPath();
        String typePath = type.getCode();
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // 使用UUID避免重名，并保留原扩展名
        String uuid = IdUtil.fastSimpleUUID();
        String uniqueFilename = uuid + "_" + filename + type.getExtension();
        return FileUtil.normalize(basePath + File.separator + typePath + File.separator + datePath + File.separator + uniqueFilename);
    }

    /**
     * 根据文件ID获取输出流
     *
     * @param fileId 文件唯一标识符
     * @return 对应文件的输出流；若文件信息不存在则返回null
     * @throws IOException 当底层IO操作发生错误时抛出
     */
    public OutputStream getOutputStream(String fileId) throws IOException {
        FileInfo fileInfo = fileInfoManager.getFileInfo(fileId);
        if (fileInfo == null) {
            log.warn("未找到文件信息: fileId={}", fileId);
            throw new IllegalArgumentException("文件信息不存在: " + fileId);
        }

        try {
            return fileStorageManager.getOutputStream(fileInfo.getFilePath());
        } catch (IOException e) {
            log.error("获取文件输出流失败: fileId={}, filePath={}", fileId, fileInfo.getFilePath(), e);
            throw new IOException("获取文件输出流失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证指定ID对应的文件是否有效存在且合法
     *
     * @param fileId 待验证的文件ID
     * @throws RuntimeException 若文件不存在、大小为0或无法读取相关信息时抛出运行时异常
     */
    public void validateGeneratedFile(String fileId) {
        FileInfo fileInfo = fileInfoManager.getFileInfo(fileId);
        if (fileInfo == null) {
            log.error("文件信息不存在: fileId={}", fileId);
            throw new RuntimeException("文件信息不存在: " + fileId);
        }

        String filePath = fileInfo.getFilePath();
        try {
            // 检查物理文件是否存在以及其大小是否大于0
            boolean exists = fileStorageManager.exists(filePath);
            if (!exists) {
                log.error("文件不存在: fileId={}, filePath={}", fileId, filePath);
                throw new RuntimeException("文件不存在: " + filePath);
            }

            long fileSize = fileStorageManager.getFileSize(filePath);
            if (fileSize <= 0) {
                log.error("文件大小为0: fileId={}, filePath={}", fileId, filePath);
                throw new RuntimeException("文件大小为0: " + filePath);
            }

            // 更新文件的实际大小至数据库记录中
            fileInfo.setFileSize(fileSize);
            fileInfoManager.updateFileInfo(fileInfo);
            log.info("文件验证成功: fileId={}, fileSize={}", fileId, fileSize);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件验证失败: fileId={}, filePath={}", fileId, filePath, e);
            throw new RuntimeException("文件验证失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文件及其信息
     *
     * @param fileId 文件ID
     */
    public void deleteFile(String fileId) {
        FileInfo fileInfo = fileInfoManager.getFileInfo(fileId);
        if (fileInfo == null) {
            log.warn("文件信息不存在，无法删除: fileId={}", fileId);
            return;
        }

        try {
            // 删除物理文件
            if (fileStorageManager.exists(fileInfo.getFilePath())) {
                fileStorageManager.deleteFile(fileInfo.getFilePath());
                log.info("文件已删除: fileId={}, filePath={}", fileId, fileInfo.getFilePath());
            }
        } catch (Exception e) {
            log.error("删除物理文件失败: fileId={}, filePath={}", fileId, fileInfo.getFilePath(), e);
        }
    }
}
