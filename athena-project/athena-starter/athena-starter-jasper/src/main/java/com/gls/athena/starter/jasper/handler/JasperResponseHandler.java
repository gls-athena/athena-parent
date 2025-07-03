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
    private final JasperHelper jasperHelper;

    /**
     * 判断是否支持返回类型
     * <p>
     * 该方法用于判断当前处理器是否支持处理给定方法参数作为返回类型
     * 主要通过检查方法是否具有特定的注解来决定
     *
     * @param returnType 方法参数对象，包含方法的参数信息
     * @return 如果支持该返回类型，则返回true；否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 检查方法是否有JasperResponse注解
        // 这里解释了为什么要检查这个注解：因为JasperResponse注解标识了该方法的返回值需要以特定的方式处理
        return returnType.hasMethodAnnotation(JasperResponse.class);
    }

    /**
     * 处理返回值的方法，专门用于处理带有@JasperResponse注解的方法
     *
     * @param returnValue  方法的返回值，将被转换为Map类型以供Jasper处理
     * @param returnType   方法参数类型，用于获取方法上的@JasperResponse注解
     * @param mavContainer ModelAndView容器，用于指示请求是否已处理
     * @param webRequest   原生的Web请求，用于获取输出流
     * @throws Exception 可能抛出的异常类型
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 标记请求已处理，避免Spring MVC继续处理
        mavContainer.setRequestHandled(true);

        // 获取方法上的@JasperResponse注解
        JasperResponse jasperResponse = returnType.getMethodAnnotation(JasperResponse.class);
        // 如果方法上没有@JasperResponse注解，则抛出异常
        if (jasperResponse == null) {
            throw new IllegalArgumentException("方法未添加@JasperResponse注解");
        }

        // 将返回值转换为Map类型，以便Jasper可以使用
        Map<String, Object> data = BeanUtil.beanToMap(returnValue);

        // 获取输出流并处理Jasper报告
        try (OutputStream outputStream = getOutputStream(webRequest, jasperResponse)) {
            jasperHelper.handle(data, outputStream, jasperResponse);
            // 记录成功处理的日志
            log.info("JasperResponseHandler: 成功处理: {}", jasperResponse.template());
        } catch (IOException e) {
            // 记录处理失败的日志
            log.error("JasperResponseHandler: 处理失败: {}", jasperResponse.template(), e);
            // 如果处理失败，则抛出运行时异常
            throw new RuntimeException("处理失败", e);
        }
    }

    /**
     * 获取用于导出报告的OutputStream对象
     * 此方法负责设置HTTP响应的相关属性，以确保报告能以正确的格式和文件名下载
     *
     * @param webRequest     包含HTTP请求相关的信息和方法
     * @param jasperResponse 包含Jasper报告的响应信息，如文件名和报告类型
     * @return OutputStream对象，用于输出报告文件
     * @throws IOException 如果无法获取OutputStream对象，则抛出此异常
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

        // 获取报告类型
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

        // 返回用于输出报告文件的OutputStream对象
        return response.getOutputStream();
    }

}
