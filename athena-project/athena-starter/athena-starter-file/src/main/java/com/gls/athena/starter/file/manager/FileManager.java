package com.gls.athena.starter.file.manager;

import cn.hutool.core.util.IdUtil;
import com.gls.athena.common.core.constant.FileTypeEnums;
import com.gls.athena.starter.file.domain.FileInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * 文件服务接口，提供文件的基本操作功能
 *
 * @author george
 */
@Component
public class FileManager {
    @Resource
    private IFileInfoManager fileInfoManager;
    @Resource
    private IFileStorageManager fileStorageManager;

    /**
     * 生成文件信息并保存
     *
     * @param type     文件类型枚举
     * @param filename 文件名
     * @return 生成的文件信息对象
     */
    public FileInfo generateFileInfo(FileTypeEnums type, String filename) {
        // 生成唯一文件ID
        String fileId = IdUtil.fastSimpleUUID();
        // 生成文件存储路径
        String filePath = fileStorageManager.generateFilePath(filename, type);
        // 计算文件访问URL的过期时间
        Date expireTime = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        // 生成文件访问URL
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
        // 保存文件信息
        fileInfoManager.saveFileInfo(fileInfo);
        return fileInfo;
    }

    /**
     * 根据文件ID获取输出流
     *
     * @param fileId 文件ID
     * @return 文件输出流，如果文件不存在则返回null
     */
    public OutputStream getOutputStream(String fileId) throws IOException {
        FileInfo fileInfo = fileInfoManager.getFileInfo(fileId);
        if (fileInfo != null) {
            return fileStorageManager.getOutputStream(fileInfo.getFilePath());
        }
        return null;
    }

    /**
     * 验证生成的文件是否有效
     *
     * @param fileId 文件ID
     * @throws RuntimeException 当文件不存在、大小为0或文件信息不存在时抛出异常
     */
    public void validateGeneratedFile(String fileId) {
        FileInfo fileInfo = fileInfoManager.getFileInfo(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("文件信息不存在");
        }

        String filePath = fileInfo.getFilePath();
        try {
            // 检查文件是否存在且大小大于0
            boolean exists = fileStorageManager.exists(filePath);
            long fileSize = exists ? fileStorageManager.getFileSize(filePath) : 0;

            if (!exists || fileSize <= 0) {
                throw new RuntimeException("文件不存在或大小为0");
            }

            // 更新文件大小信息
            fileInfo.setFileSize(fileSize);
            fileInfoManager.updateFileInfo(fileInfo);
        } catch (Exception e) {
            // 处理fileStorageManager可能抛出的异常
            throw new RuntimeException("文件验证失败");
        }
    }
}
