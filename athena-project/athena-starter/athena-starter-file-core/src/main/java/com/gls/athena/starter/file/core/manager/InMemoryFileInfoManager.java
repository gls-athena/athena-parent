package com.gls.athena.starter.file.core.manager;

import com.gls.athena.starter.file.core.domain.FileInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的文件信息管理器实现类
 * 使用ConcurrentHashMap作为存储介质，提供线程安全的文件信息存储和检索功能
 *
 * @author george
 */
public class InMemoryFileInfoManager implements IFileInfoManager {
    /**
     * 内存存储的ConcurrentHashMap
     */
    private final ConcurrentHashMap<String, FileInfo> fileInfoStorage = new ConcurrentHashMap<>();

    /**
     * 保存文件信息
     * 将文件信息按照文件ID作为键存入内存存储中
     *
     * @param fileInfo 文件信息对象，包含文件的详细信息
     */
    @Override
    public void saveFileInfo(FileInfo fileInfo) {
        fileInfoStorage.put(fileInfo.getFileId(), fileInfo);
    }

    /**
     * 根据文件ID获取文件信息
     * 从内存存储中检索指定文件ID对应的文件信息
     *
     * @param fileId 文件唯一标识符
     * @return 对应的文件信息对象，如果不存在则返回null
     */
    @Override
    public FileInfo getFileInfo(String fileId) {
        return fileInfoStorage.get(fileId);
    }

    /**
     * 更新文件信息
     * 将新的文件信息按照文件ID作为键更新到内存存储中
     *
     * @param fileInfo 更新后的文件信息对象
     */
    @Override
    public void updateFileInfo(FileInfo fileInfo) {
        fileInfoStorage.put(fileInfo.getFileId(), fileInfo);
    }
}

