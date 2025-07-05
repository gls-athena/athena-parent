package com.gls.athena.starter.word.context;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.Map;

/**
 * 模板处理上下文
 * 使用建造者模式构建复杂的上下文对象
 *
 * @author athena
 */
@Data
@Builder
public class TemplateContext {

    /**
     * 待处理的文档
     */
    private XWPFDocument document;

    /**
     * 数据上下文
     */
    private Map<String, Object> data;

    /**
     * 模板路径
     */
    private String templatePath;

    /**
     * 是否启用调试模式
     */
    @Builder.Default
    private boolean debug = false;

    /**
     * 最大包含深度
     */
    @Builder.Default
    private int maxIncludeDepth = 10;

    /**
     * 空值的默认显示
     */
    @Builder.Default
    private String nullValueDisplay = "";
}
