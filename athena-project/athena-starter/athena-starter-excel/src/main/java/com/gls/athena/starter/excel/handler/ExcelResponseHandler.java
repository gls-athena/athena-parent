package com.gls.athena.starter.excel.handler;

import cn.hutool.core.util.StrUtil;
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
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";
    private static final int MAX_FILENAME_LENGTH = 255;

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
        if (excelResponse == null) {
            throw new IllegalArgumentException("方法返回值必须使用@ExcelResponse注解标记");
        }

        // 创建Excel输出流并写入数据
        try (OutputStream outputStream = getOutputStream(webRequest, excelResponse.filename(), excelResponse.excelType().getValue())) {
            ExcelUtil.exportToExcel(returnValue, outputStream, excelResponse);
        } catch (IOException e) {
            log.error("导出Excel文件时发生错误", e);
            throw e;
        }
    }

    /**
     * 获取用于Excel文件下载的输出流
     *
     * @param webRequest NativeWebRequest对象，用于获取HttpServletResponse
     * @param fileName   要下载的文件名（不含扩展名）
     * @param excelType  Excel文件扩展名（如".xlsx"）
     * @return OutputStream 用于写入Excel文件数据的输出流
     * @throws IOException              如果获取输出流失败
     * @throws IllegalArgumentException 如果参数无效或无法获取HttpServletResponse
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, String fileName, String excelType) throws IOException {
        // 参数校验
        if (StrUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (StrUtil.isEmpty(excelType)) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        if (fileName.length() > MAX_FILENAME_LENGTH - excelType.length()) {
            throw new IllegalArgumentException("文件名过长");
        }

        // 获取并验证响应对象
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException("无法获取HttpServletResponse");
        }

        // 设置响应头
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 安全编码文件名
        String sanitizedFileName = fileName.replaceAll("[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]", "_");
        String encodedFileName = URLUtil.encode(sanitizedFileName, StandardCharsets.UTF_8);
        String fullFileName = encodedFileName + excelType;

        // 设置内容处置和跨域头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, fullFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        return response.getOutputStream();
    }

}
