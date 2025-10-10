package com.gls.athena.starter.file.manager;

import com.gls.athena.starter.data.redis.support.RedisUtil;
import com.gls.athena.starter.file.domain.FileInfo;

/**
 * Redis文件信息管理器实现类
 * 该类用于通过Redis存储和管理文件信息
 *
 * @author george
 */
public class RedisFileInfoManager implements IFileInfoManager {

    private static final String FILE_INFO = "file:info";

    /**
     * 保存文件信息到Redis中
     *
     * @param fileInfo 文件信息对象，包含要保存的文件详细信息
     */
    @Override
    public void saveFileInfo(FileInfo fileInfo) {
        RedisUtil.setCacheTableRow(FILE_INFO, fileInfo.getFileId(), fileInfo);
    }

    /**
     * 根据文件ID从Redis中获取文件信息
     *
     * @param fileId 文件唯一标识符
     * @return FileInfo 文件信息对象，如果不存在则返回null
     */
    @Override
    public FileInfo getFileInfo(String fileId) {
        return RedisUtil.getCacheTableRow(FILE_INFO, fileId, FileInfo.class);
    }

    /**
     * 更新Redis中的文件信息
     *
     * @param fileInfo 文件信息对象，包含要更新的文件详细信息
     */
    @Override
    public void updateFileInfo(FileInfo fileInfo) {
        RedisUtil.setCacheTableRow(FILE_INFO, fileInfo.getFileId(), fileInfo);
    }
}

