package com.gls.athena.starter.file.manager;

import com.gls.athena.starter.file.domain.FileInfo;

/**
 * 文件信息管理接口
 * 定义了文件信息的基本操作方法，包括保存、获取和更新文件信息
 *
 * @author george
 */
public interface IFileInfoManager {
    /**
     * 保存文件信息
     * 将文件信息持久化存储
     *
     * @param fileInfo 文件信息对象，包含文件的基本信息和元数据
     */
    void saveFileInfo(FileInfo fileInfo);

    /**
     * 根据文件ID获取文件信息
     * 从存储中检索指定文件的详细信息
     *
     * @param fileId 文件唯一标识符
     * @return FileInfo 文件信息对象，如果未找到则返回null
     */
    FileInfo getFileInfo(String fileId);

    /**
     * 更新文件信息
     * 修改已存在的文件信息并保存到存储中
     *
     * @param fileInfo 文件信息对象，包含更新后的文件信息
     */
    void updateFileInfo(FileInfo fileInfo);
}

