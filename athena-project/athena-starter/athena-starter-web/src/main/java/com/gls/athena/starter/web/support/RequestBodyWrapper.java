package com.gls.athena.starter.web.support;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestBodyWrapper 用于解决流只能读取一次的问题
 *
 * @author george
 */
@Slf4j
public class RequestBodyWrapper extends HttpServletRequestWrapper {

    private static final String FORM_DATA_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    /**
     * 请求体
     */
    private final String body;

    /**
     * 参数映射
     */
    private final Map<String, String[]> parameterMap;

    /**
     * 构造函数
     *
     * @param request 请求
     */
    public RequestBodyWrapper(HttpServletRequest request) {
        super(request);
        this.parameterMap = new HashMap<>();
        this.body = getBodyString(request);
        parseRequestBody(request);
    }

    /**
     * 解析请求体
     *
     * @param request 请求
     */
    private void parseRequestBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (StrUtil.isNotEmpty(body) && contentType != null
                && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            parseFormData(body);
        }
    }

    /**
     * 解析表单数据
     *
     * @param body 请求体
     */
    private void parseFormData(String body) {
        String[] params = body.split(FORM_DATA_SEPARATOR);
        for (String param : params) {
            if (StrUtil.isEmpty(param)) {
                continue;
            }

            String[] keyValue = param.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = decode(keyValue[0]);
            String value = decode(keyValue[1]);

            if (key == null) {
                continue;
            }

            addParameter(key, value);
        }
    }

    /**
     * 添加参数
     *
     * @param key   键
     * @param value 值
     */
    private void addParameter(String key, String value) {
        String[] existingValues = parameterMap.get(key);
        if (existingValues != null) {
            String[] newValues = new String[existingValues.length + 1];
            System.arraycopy(existingValues, 0, newValues, 0, existingValues.length);
            newValues[existingValues.length] = value;
            parameterMap.put(key, newValues);
        } else {
            parameterMap.put(key, new String[]{value});
        }
    }

    /**
     * 解码
     *
     * @param value 值
     * @return 解码后的值
     */
    private String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLDecoder.decode(value, getCharacterEncoding());
        } catch (Exception e) {
            log.warn("Failed to decode value: {}", value, e);
            return value;
        }
    }

    /**
     * 获取请求体字符串
     *
     * @param request 请求
     * @return 请求体字符串
     */
    private String getBodyString(HttpServletRequest request) {
        try (ServletInputStream inputStream = request.getInputStream()) {
            return IoUtil.read(inputStream, CharsetUtil.charset(getCharacterEncoding()));
        } catch (IOException e) {
            log.error("Failed to read request body", e);
            return "";
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                body.getBytes(getCharacterEncoding())
        );

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException("ReadListener is not supported");
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 获取读取器
     *
     * @return 读取器
     * @throws IOException IO异常
     */
    @Override
    public BufferedReader getReader() throws IOException {
        // 返回读取器
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    /**
     * 获取参数
     *
     * @param name 参数名
     * @return 参数值
     */
    @Override
    public String getParameter(String name) {
        String[] values = parameterMap.get(name);
        return values != null && values.length > 0 ? values[0] : super.getParameter(name);
    }

    /**
     * 获取参数映射
     *
     * @return 参数映射
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap.isEmpty() ? super.getParameterMap() : parameterMap;
    }

    /**
     * 获取参数名枚举
     *
     * @return 参数名枚举
     */
    @Override
    public Enumeration<String> getParameterNames() {
        return parameterMap.isEmpty() ? super.getParameterNames() : Collections.enumeration(parameterMap.keySet());
    }

    /**
     * 获取参数值
     *
     * @param name 参数名
     * @return 参数值
     */
    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.getOrDefault(name, super.getParameterValues(name));
    }
}
