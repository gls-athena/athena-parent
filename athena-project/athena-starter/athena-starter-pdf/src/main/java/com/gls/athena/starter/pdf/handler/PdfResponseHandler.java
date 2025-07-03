package com.gls.athena.starter.pdf.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.support.PdfHelper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * PDF响应处理器
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class PdfResponseHandler implements HandlerMethodReturnValueHandler {
    /**
     * PDF属性
     */
    private final PdfHelper pdfHelper;

    /**
     * 检查方法是否带有@PdfResponse注解
     *
     * @param returnType 方法参数
     * @return 是否支持
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        //  检查方法是否带有@PdfResponse注
        log.debug("检查方法返回类型是否支持PDF响应: {}", returnType);
        return returnType.hasMethodAnnotation(PdfResponse.class);
    }

    /**
     * 处理方法返回值
     *
     * @param returnValue  方法返回值
     * @param returnType   方法参数
     * @param mavContainer 模型和视图容器
     * @param webRequest   web请求
     * @throws Exception 异常
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 设置请求已处理
        mavContainer.setRequestHandled(true);
        // 获取方法上的@PdfResponse注解
        PdfResponse pdfResponse = returnType.getMethodAnnotation(PdfResponse.class);
        if (pdfResponse == null) {
            throw new IllegalArgumentException("方法未添加@PdfResponse注解");
        }

        Map<String, Object> data = BeanUtil.beanToMap(returnValue);
        try (OutputStream outputStream = getOutputStream(webRequest, pdfResponse.filename())) {
            switch (pdfResponse.templateType()) {
                case HTML:
                    // 如果是HTML模板，直接渲染HTML到PDF
                    pdfHelper.handleHtmlTemplate(data, outputStream, pdfResponse);
                    break;
                case PDF:
                    // 如果是PDF模板，填充数据到PDF
                    pdfHelper.handlePdfTemplate(data, outputStream, pdfResponse);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的模板类型: " + pdfResponse.templateType());
            }
        }
    }

    /**
     * 获取用于输出PDF文件的OutputStream
     *
     * @param webRequest NativeWebRequest对象，用于获取HttpServletResponse
     * @param fileName   要输出的文件名（自动补全.pdf扩展名）
     * @return 响应输出流，用于写入PDF文件内容
     * @throws IOException              如果获取输出流失败
     * @throws IllegalArgumentException 如果文件名为空
     * @throws IllegalStateException    如果无法获取HttpServletResponse对象
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, String fileName) throws IOException {
        // 参数校验：确保文件名不为空
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 从webRequest中获取HttpServletResponse对象并进行非空校验
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalStateException("无法获取HttpServletResponse对象");
        }

        // 设置响应头：字符编码、内容类型为PDF
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/pdf");

        // 处理文件名：URL编码并确保有.pdf扩展名
        String encodedFileName = URLUtil.encode(fileName, StandardCharsets.UTF_8);
        String finalFileName = encodedFileName.endsWith(".pdf") ? encodedFileName : encodedFileName + ".pdf";

        // 设置Content-Disposition头（附件下载）和CORS相关头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + finalFileName);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

}
