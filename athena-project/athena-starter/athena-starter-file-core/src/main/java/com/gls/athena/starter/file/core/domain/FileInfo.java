package com.gls.athena.starter.file.core.domain;

import com.gls.athena.common.core.constant.FileTypeEnums;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件信息实体类
 * <p>
 * 用于封装文件的基本信息，包括文件标识、名称、路径、URL、类型和大小等属性。
 * 实现了Serializable接口，支持序列化操作。
 * </p>
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class FileInfo implements Serializable {

    /**
     * 文件唯一标识符
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String filePath;
    /**
     * 文件类型
     */
    private FileTypeEnums fileType;

    /**
     * 文件大小（字节）
     */
    private long fileSize;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件访问URL过期时间
     */
    private Date fileUrlExpireTime;

}
