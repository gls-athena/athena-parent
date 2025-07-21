package com.gls.athena.starter.pdf.generator;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.PdfProperties;
import com.gls.athena.starter.pdf.util.PdfUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * 默认的PDF生成器实现类
 * 使用Lombok的@Slf4j注解引入日志对象
 * 通过Spring的@Component注解将该类标记为Spring管理的Bean
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultPdfGenerator implements PdfGenerator {

    /**
     * PDF属性配置对象，用于获取PDF生成所需的配置信息
     */
    @Resource
    private PdfProperties pdfProperties;

    /**
     * 根据提供的数据生成PDF
     *
     * @param data         数据对象，用于生成PDF的内容
     * @param pdfResponse  PDF响应注解，包含生成PDF的额外信息
     * @param outputStream 输出流，用于接收生成的PDF数据
     * @throws Exception 如果PDF生成过程中发生错误
     */
    @Override
    public void generate(Object data, PdfResponse pdfResponse, OutputStream outputStream) throws Exception {
        // 将数据转换为字符串形式的HTML内容
        String html = (String) data;
        // 创建PDF渲染器对象
        ITextRenderer renderer = new ITextRenderer();
        // 加载自定义字体到渲染器
        PdfUtil.addClasspathFonts(renderer, pdfProperties.getFontPath());
        // 将HTML内容加载到渲染器
        renderer.setDocumentFromString(html);
        // 布局PDF页面
        renderer.layout();
        // 创建PDF并输出到输出流
        renderer.createPDF(outputStream);
        // 完成PDF生成过程
        renderer.finishPDF();
    }

    /**
     * 判断当前生成器是否支持指定的PDF响应
     *
     * @param pdfResponse PDF响应注解，包含生成PDF的额外信息
     * @return 如果当前生成器支持指定的PDF响应，则返回true；否则返回false
     */
    @Override
    public boolean supports(PdfResponse pdfResponse) {
        // 当模板路径为空且指定的生成器为PdfGenerator.class时，表示支持
        return StrUtil.isBlank(pdfResponse.template())
                && pdfResponse.generator().equals(PdfGenerator.class);
    }
}
