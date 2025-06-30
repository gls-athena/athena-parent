package com.gls.athena.starter.excel.handler;

import cn.hutool.core.util.URLUtil;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.support.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Excel响应处理器
 * 处理带有@ExcelResponse注解的方法返回值，将数据写入Excel文件并返回给客户端。
 *
 * @author george
 */
@Slf4j
public class ExcelResponseHandler implements HandlerMethodReturnValueHandler {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";

    /**
     * 判断当前处理器是否支持给定的方法返回类型
     *
     * @param returnType 方法返回类型参数对象，包含方法元数据信息
     * @return boolean 返回true表示支持该返回类型（方法带有@ExcelResponse注解），否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 通过检查方法是否包含@ExcelResponse注解来确定是否支持该返回类型
        return returnType.hasMethodAnnotation(ExcelResponse.class);
    }

    /**
     * 处理Excel响应返回值，将数据写入Excel并输出到响应流中
     *
     * @param returnValue  控制器方法返回的数据对象
     * @param returnType   方法参数信息，包含方法注解等元数据
     * @param mavContainer ModelAndView容器，用于标记请求处理状态
     * @param webRequest   原生Web请求对象，用于获取输出流
     * @throws Exception 当Excel写入过程中发生错误时抛出
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 标记请求已处理
        mavContainer.setRequestHandled(true);
        // 获取Excel响应的配置信息
        ExcelResponse excelResponse = returnType.getMethodAnnotation(ExcelResponse.class);
        log.info("ExcelResponseHandler: {}", excelResponse);
        // 创建Excel输出流并写入数据
        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            ExcelUtil.exportToExcel(returnValue, outputStream, excelResponse);
        } catch (Exception e) {
            log.error("ExcelResponseHandler: {}", e.getMessage(), e);
        }
    }

    private OutputStream getOutputStream(NativeWebRequest webRequest, String fileName, String excelType) throws IOException {
        // 参数校验
        if (fileName == null || excelType == null) {
            throw new IllegalArgumentException("文件名或文件类型不能为空");
        }

        // 获取并验证HttpServletResponse对象
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse为空");
        }

        // 设置响应头：内容类型、编码、文件名和跨域头
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String encodedFileName = URLUtil.encode(fileName, StandardCharsets.UTF_8);
        String name = encodedFileName + excelType;
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + name);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return response.getOutputStream();
    }

}
