package com.gls.athena.starter.word.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.support.WordHelper;
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
 * Word响应处理器
 * 处理带有@WordResponse注解的方法返回值，将数据写入Word文件并返回给客户端。
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class WordResponseHandler implements HandlerMethodReturnValueHandler {

    private final WordHelper wordHelper;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(WordResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);

        WordResponse wordResponse = returnType.getMethodAnnotation(WordResponse.class);
        if (wordResponse == null) {
            throw new IllegalArgumentException("方法未添加@WordResponse注解");
        }

        Map<String, Object> data = BeanUtil.beanToMap(returnValue);
        try (OutputStream outputStream = getOutputStream(webRequest, wordResponse.filename())) {
            switch (wordResponse.templateType()) {
                case HTML:
                    wordHelper.handleHtmlTemplate(data, outputStream, wordResponse);
                    break;
                case DOCX:
                    wordHelper.handleDocxTemplate(data, outputStream, wordResponse);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的模板类型: " + wordResponse.templateType());
            }
        }

    }

    /**
     * 获取用于输出的流对象，并设置HTTP响应的相关信息以下载文件
     * 此方法主要用于准备下载文件时的HTTP响应，包括设置响应类型、字符编码、文件名等
     *
     * @param webRequest Web请求对象，用于获取HttpServletResponse
     * @param filename   文件名，用于设置Content-Disposition头中的文件名
     * @return OutputStream对象，用于输出文件数据
     * @throws IOException              如果无法获取OutputStream时抛出此异常
     * @throws IllegalArgumentException 如果文件名为空时抛出此异常
     * @throws IllegalStateException    如果无法获取HttpServletResponse时抛出此异常
     */
    private OutputStream getOutputStream(NativeWebRequest webRequest, String filename) throws IOException {
        // 检查文件名是否为空
        if (StrUtil.isBlank(filename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        // 获取HttpServletResponse对象
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        // 检查是否成功获取HttpServletResponse对象
        if (response == null) {
            throw new IllegalStateException("无法获取HttpServletResponse");
        }
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 处理文件名，将文件名编码为UTF-8，并添加.docx后缀
        String encodedFileName = URLUtil.encode(filename, StandardCharsets.UTF_8);
        String finalFileName = encodedFileName.endsWith(".docx") ? encodedFileName : encodedFileName + ".docx";

        // 设置Content-Disposition头（附件下载）和CORS相关头
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + finalFileName);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        // 返回OutputStream对象，用于输出文件数据
        return response.getOutputStream();
    }

}
