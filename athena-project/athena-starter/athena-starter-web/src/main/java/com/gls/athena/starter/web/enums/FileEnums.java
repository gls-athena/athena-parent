package com.gls.athena.starter.web.enums;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author george
 */

@Getter
@RequiredArgsConstructor
public enum FileEnums implements IEnum<String> {
    /**
     * PDF文件
     */
    PDF("pdf", "PDF文件", ".pdf", "application/pdf"),
    /**
     * XLSX文件
     */
    XLSX("xlsx", "XLSX文件", ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    /**
     * XLS文件
     */
    XLS("xls", "XLS文件", ".xls", "application/vnd.ms-excel"),
    /**
     * DOCX文件
     */
    DOCX("docx", "DOCX文件", ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    /**
     * DOC文件
     */
    DOC("doc", "DOC文件", ".doc", "application/msword"),
    /**
     * PPTX文件
     */
    PPTX("pptx", "PPTX文件", ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    /**
     * PPT文件
     */
    PPT("ppt", "PPT文件", ".ppt", "application/vnd.ms-powerpoint"),
    /**
     * HTML文件
     */
    HTML("html", "HTML文件", ".html", "text/html"),
    /**
     * XML文件
     */
    XML("xml", "XML文件", ".xml", "application/xml"),
    /**
     * 默认文件类型
     */
    DEFAULT("default", "默认文件类型", ".txt", "application/octet-stream"),
    ;
    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;
    /**
     * 文件扩展名
     */
    private final String extension;
    /**
     * 内容类型
     */
    private final String contentType;

    public static FileEnums getFileEnums(String extension) {
        return Arrays.stream(FileEnums.values())
                .filter(fileEnums -> fileEnums.extension.equals(extension))
                .findFirst()
                .orElse(DEFAULT);
    }
}
