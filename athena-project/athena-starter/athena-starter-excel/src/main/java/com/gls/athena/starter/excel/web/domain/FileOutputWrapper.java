package com.gls.athena.starter.excel.web.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.OutputStream;

/**
 * 文件输出流包装类
 * 用于封装文件输出流和文件路径信息的包装类
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class FileOutputWrapper {

    /**
     * 输出流对象
     * 用于文件内容的输出操作
     */
    private OutputStream outputStream;

    /**
     * 文件路径
     * 指定文件的存储路径
     */
    private String filePath;
}

