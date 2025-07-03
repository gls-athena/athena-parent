package com.gls.athena.starter.pdf.support;

import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.strategy.ITemplateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * PDF处理助手类，使用策略模式管理不同类型的PDF模板处理
 *
 * @author george
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfHelper {

    private final List<ITemplateHandler> templateHandlers;

    /**
     * 根据PdfResponse获取对应的模板处理器
     *
     * @param pdfResponse 包含PDF响应信息的对象，用于确定使用哪种模板类型
     * @return ITemplateHandler接口的实现类，用于处理特定的模板类型
     * <p>
     * 该方法通过流式处理筛选出能够支持给定PdfResponse模板类型的模板处理器
     * 如果没有找到支持的模板处理器，则抛出IllegalArgumentException异常
     */
    private ITemplateHandler getTemplateHandler(PdfResponse pdfResponse) {
        return templateHandlers.stream()
                .filter(handler -> handler.supports(pdfResponse.templateType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的PDF模板类型: " + pdfResponse.templateType()));
    }

    /**
     * 处理PDF生成请求
     * <p>
     * 该方法负责将给定的数据根据指定的PDF模板处理并输出到指定的流中
     * 它首先记录了正在处理的PDF模板的名称，然后获取相应的模板处理器，
     * 最后使用该处理器处理数据并生成PDF文档
     *
     * @param data         包含PDF模板所需数据的映射表
     * @param outputStream 用于输出生成的PDF数据的流
     * @param pdfResponse  包含PDF模板信息及其它响应数据的对象
     * @throws IOException 如果在处理数据或输出过程中发生I/O错误
     */
    public void handle(Map<String, Object> data, OutputStream outputStream, PdfResponse pdfResponse) throws IOException {
        // 记录正在处理的PDF模板的名称
        log.debug("处理PDF模板: {}", pdfResponse.template());

        // 获取处理PDF模板的处理器实例
        ITemplateHandler handler = getTemplateHandler(pdfResponse);

        // 使用处理器处理数据并生成PDF文档
        handler.handle(data, outputStream, pdfResponse);
    }
}
