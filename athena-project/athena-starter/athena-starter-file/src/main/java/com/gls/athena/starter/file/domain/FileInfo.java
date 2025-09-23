package com.gls.athena.starter.file.domain;

import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.core.constant.FileTypeEnums;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件表视图对象
 * <p>
 * 该类用于表示文件表的视图对象，封装了文件表的相关属性
 * </p>
 *
 * @author athena 自动生成
 * @version 0.0.1-SNAPSHOT
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfo extends BaseVo {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小，单位字节
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private FileTypeEnums fileType;

}
