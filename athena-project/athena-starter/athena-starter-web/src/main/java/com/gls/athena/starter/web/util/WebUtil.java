package com.gls.athena.starter.web.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import com.gls.athena.starter.web.enums.FileEnums;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Web工具类，提供与HTTP请求和响应相关的实用方法。
 *
 * @author george
 */
@UtilityClass
public class WebUtil {
    /**
     * HTTP响应头Content-Disposition的格式模板
     */
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=%s";

    /**
     * 文件名最大长度限制
     */
    private static final int MAX_FILENAME_LENGTH = 255;

    /**
     * 文件名中的非法字符正则表达式
     */
    private static final String ILLEGAL_FILENAME_CHARS = "[\\x00-\\x1F\\x7F\"\\\\/:*?<>|]";
    /**
     * 缓存控制头部值，禁止缓存
     */
    private static final String CACHE_CONTROL_HEADER = "no-cache, no-store, must-revalidate";
    /**
     * pragma头部值，禁止缓存
     */
    private static final String PRAGMA_HEADER = "no-cache";
    /**
     * expires头部值，设置为0表示立即过期
     */
    private static final String EXPIRES_HEADER = "0";

    /**
     * 将HttpServletRequest中的请求参数转换为MultiValueMap结构。
     *
     * @param request HttpServletRequest对象，包含客户端请求的参数。
     * @return LinkedMultiValueMap<String, String> 保持参数顺序的MultiValueMap结构，
     * 其中键为参数名，值为参数值列表（支持多值参数）。
     * <p>
     * 实现逻辑：
     * 1. 通过request.getParameterMap()获取原始参数映射。
     * 2. 将每个参数值数组转换为ArrayList，以适配MultiValueMap的值类型要求。
     * 3. 使用Linked结构保持参数原始顺序。
     */
    public MultiValueMap<String, String> getParameterMap(HttpServletRequest request) {
        MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((key, values) ->
                parameterMap.put(key, new ArrayList<>(Arrays.asList(values)))
        );
        return parameterMap;
    }

    /**
     * 获取当前线程绑定的HttpServletRequest对象。
     *
     * @return Optional<HttpServletRequest> 包含当前请求的Optional对象，如果不存在则返回空Optional。
     */
    public Optional<HttpServletRequest> getRequest() {
        return Optional.of(RequestContextHolder.currentRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * 获取当前线程绑定的HttpServletResponse对象。
     *
     * @return Optional<HttpServletResponse> 包含当前响应的Optional对象，如果不存在则返回空Optional。
     */
    public Optional<HttpServletResponse> getResponse() {
        return Optional.of(RequestContextHolder.currentRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getResponse);
    }

    /**
     * 获取指定名称的请求参数值。首先从URL参数中查找，如果未找到，则从请求体中查找。
     *
     * @param request       HttpServletRequest对象，包含客户端请求的参数。
     * @param parameterName 参数名称。
     * @return String 请求参数的值，如果未找到则返回null。
     */
    public String getParameter(HttpServletRequest request, String parameterName) {
        String parameter = WebUtils.findParameterValue(request, parameterName);
        if (StrUtil.isNotBlank(parameter)) {
            return parameter;
        }
        return getParameterByBody(request, parameterName);
    }

    /**
     * 从请求体中获取指定名称的参数值。如果请求体是JSON格式，则解析JSON并获取指定参数。
     *
     * @param request       HttpServletRequest对象，包含客户端请求的参数。
     * @param parameterName 参数名称。
     * @return String 请求体中的参数值，如果未找到或请求体不是JSON格式则返回null。
     */
    public String getParameterByBody(HttpServletRequest request, String parameterName) {
        String body = JakartaServletUtil.getBody(request);
        if (StrUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
            return JSONUtil.parseObj(body).getStr(parameterName);
        }
        return null;
    }

    /**
     * 创建用于文件下载的输出流
     *
     * @param webRequest Web请求对象，用于获取HTTP响应
     * @param fileName   文件名，用于验证并设置响应头
     * @param fileEnums  文件类型，用于确定文件类型和内容类型
     * @return OutputStream对象，用于输出文件数据
     * @throws IOException 如果无法创建输出流
     */
    public OutputStream createOutputStream(NativeWebRequest webRequest, String fileName, FileEnums fileEnums) throws IOException {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse获取失败");
        }

        return createOutputStream(response, fileName, fileEnums);
    }

    /**
     * 创建用于下载文件的输出流
     * 此方法根据提供的文件名和文件类型，在HTTP响应中设置适当的头信息，并返回一个输出流用于写入文件数据
     *
     * @param response  HTTP响应对象，用于设置响应头和创建输出流
     * @param fileName  要下载的文件名，不包含扩展名
     * @param fileEnums 文件类型枚举，用于确定文件扩展名和MIME类型
     * @return 一个输出流，用于向响应中写入文件数据
     * @throws IOException 如果创建输出流时发生错误
     */
    public OutputStream createOutputStream(HttpServletResponse response, String fileName, FileEnums fileEnums) throws IOException {
        // 检查文件名是否为空
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        // 检查文件类型是否支持
        if (fileEnums == null) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        // 清理非法字符，拼接扩展名
        String sanitizedFileName = fileName.replaceAll(ILLEGAL_FILENAME_CHARS, "_");
        String fullFileName = sanitizedFileName + fileEnums.getExtension();

        // 检查文件名长度是否过长
        if (fullFileName.length() > MAX_FILENAME_LENGTH) {
            throw new IllegalArgumentException("文件名过长");
        }

        // URL编码
        String encodedFileName = URLUtil.encode(fullFileName, StandardCharsets.UTF_8);

        // 编码后再次检查文件名长度
        if (encodedFileName.length() > MAX_FILENAME_LENGTH) {
            throw new IllegalArgumentException("编码后文件名过长");
        }

        // 设置响应头
        response.setContentType(fileEnums.getContentType() + "; charset=UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FORMAT, encodedFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        // 设置缓存控制头
        response.setHeader(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_HEADER);
        response.setHeader(HttpHeaders.PRAGMA, PRAGMA_HEADER);
        response.setHeader(HttpHeaders.EXPIRES, EXPIRES_HEADER);

        // 返回用于写入文件数据的输出流
        return response.getOutputStream();
    }

    /**
     * 创建输出流方法
     * 该方法用于根据文件类型和文件名创建一个合适的输出流
     * 主要用于处理文件下载或生成时的流创建
     *
     * @param webRequest 用于处理Web请求的对象，提供对HTTP请求的访问
     * @param filename   文件名，用于设置Content-Disposition头中的文件名
     * @param fileType   文件类型，用于确定使用哪种文件枚举类型
     * @return OutputStream 返回一个输出流对象，用于写入文件数据
     * @throws IOException 如果创建输出流时发生I/O错误
     */
    public OutputStream createOutputStream(NativeWebRequest webRequest, String filename, String fileType) throws IOException {
        // 根据文件类型获取对应的文件枚举类型
        FileEnums fileEnums = FileEnums.getFileEnums(fileType);
        // 调用WebUtil工具类的方法创建输出流
        return createOutputStream(webRequest, filename, fileEnums);
    }

    /**
     * 从NativeWebRequest对象中获取指定名称的MultipartFile对象
     * <p>
     * 此方法主要用于在处理文件上传时，从HTTP请求中提取出指定名称的文件对象
     * 它首先验证文件名参数是否为空，然后尝试从请求中获取对应的文件对象，
     * 如果文件名为空或文件不存在，则抛出相应的异常
     *
     * @param webRequest NativeWebRequest对象，用于处理Web请求
     * @param fileName   文件名，用于指定需要获取的文件
     * @return MultipartFile对象，表示上传的文件
     */
    public MultipartFile getMultipartFile(NativeWebRequest webRequest, String fileName) {
        // 验证文件名参数
        if (StrUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 获取多部分请求对象
        MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest == null) {
            throw new IllegalArgumentException("当前请求不是多部分请求");
        }

        // 获取指定名称的文件
        MultipartFile file = multipartRequest.getFile(fileName);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不存在或为空: " + fileName);
        }

        return file;
    }

    public Optional<HttpSession> getSession() {
        return getRequest().map(HttpServletRequest::getSession);
    }
}
