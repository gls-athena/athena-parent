package com.gls.athena.starter.jasper.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.config.ReportType;
import com.gls.athena.starter.jasper.support.JasperHelper;
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
public class JasperResponseHandler implements HandlerMethodReturnValueHandler {
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    /**
     * PDF属性
     */
    private final JasperHelper jasperHelper;

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
        return returnType.hasMethodAnnotation(JasperResponse.class);
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
        JasperResponse jasperResponse = returnType.getMethodAnnotation(JasperResponse.class);
        if (jasperResponse == null) {
            throw new IllegalArgumentException("方法未添加@PdfResponse注解");
        }

        Map<String, Object> data = BeanUtil.beanToMap(returnValue);
        try (OutputStream outputStream = getOutputStream(webRequest, jasperResponse)) {
            // 使用PDF助手处理PDF响应
            jasperHelper.handle(data, outputStream, jasperResponse);
            log.info("PDFResponseHandler: 成功处理PDF模板: {}", jasperResponse.template());
        } catch (IOException e) {
            log.error("PDFResponseHandler: 处理PDF模板失败: {}", jasperResponse.template(), e);
            throw new RuntimeException("处理PDF模板失败", e);
        }
    }

    /**
     * 获取用于输出PDF文件的OutputStream
     *
     * @param webRequest NativeWebRequest对象，用于获取HttpServletResponse
     * @return 响应输出流，用于写入PDF文件内容
     * @throws IOException              如果获取输出流失败
     * @throws IllegalArgumentException 如果文件名为空
     * @throws IllegalStateException    如果无法获取HttpServletResponse对象
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, JasperResponse jasperResponse) throws IOException {
        // 参数校验：确保文件名不为空
        String fileName = jasperResponse.filename();
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 从webRequest中获取HttpServletResponse对象并进行非空校验
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalStateException("无法获取HttpServletResponse对象");
        }
        ReportType reportType = jasperResponse.reportType();
        // 设置响应头：字符编码、内容类型为PDF
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(reportType.getContentType());

        // 安全编码文件名
        String sanitizedFileName = fileName.replaceAll("[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]", "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + reportType.getExtension();

        // 设置内容处置和跨域头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

}
