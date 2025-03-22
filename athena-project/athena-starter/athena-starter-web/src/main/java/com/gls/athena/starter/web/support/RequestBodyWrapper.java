package com.gls.athena.starter.web.support;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

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
     * <p>
     * 该方法用于根据请求的内容类型（Content-Type）解析请求体。如果请求体不为空且内容类型为
     * "application/x-www-form-urlencoded"，则调用parseFormData方法解析表单数据。
     *
     * @param request 包含请求信息的HttpServletRequest对象，用于获取请求体和内容类型。
     */
    private void parseRequestBody(HttpServletRequest request) {
        // 获取请求的内容类型
        String contentType = request.getContentType();

        // 检查请求体是否为空且内容类型是否为"application/x-www-form-urlencoded"
        if (StrUtil.isNotEmpty(body) && contentType != null
                && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            // 解析表单数据
            parseFormData(body);
        }
    }

    /**
     * 解析表单数据
     * <p>
     * 该方法用于解析HTTP请求体中的表单数据，将其拆分为键值对，并解码后存储。
     *
     * @param body 请求体，包含表单数据的字符串，通常以特定的分隔符分隔键值对。
     */
    private void parseFormData(String body) {
        // 将请求体按表单数据分隔符拆分为多个参数
        String[] params = body.split(FORM_DATA_SEPARATOR);

        // 遍历每个参数，解析并存储键值对
        for (String param : params) {
            // 跳过空参数
            if (StrUtil.isEmpty(param)) {
                continue;
            }

            // 将参数按键值对分隔符拆分为键和值
            String[] keyValue = param.split(KEY_VALUE_SEPARATOR, 2);
            // 如果拆分后的键值对不完整，跳过
            if (keyValue.length != 2) {
                continue;
            }

            // 解码键，如果解码失败则跳过
            String key = decode(keyValue[0]);
            if (key == null) {
                continue;
            }

            // 解码值，并将键值对添加到参数列表中
            String value = decode(keyValue[1]);
            addParameter(key, value);
        }
    }

    /**
     * 添加参数到参数映射中。如果键已经存在，则将新值追加到现有值的数组中；如果键不存在，则创建一个新的数组并存储值。
     *
     * @param key   参数的键，用于标识参数。
     * @param value 参数的值，将与键关联存储。
     */
    private void addParameter(String key, String value) {
        // 获取与键关联的现有值数组
        String[] existingValues = parameterMap.get(key);

        // 如果键已存在，则扩展数组并将新值追加到数组末尾
        if (existingValues != null) {
            existingValues = Arrays.copyOf(existingValues, existingValues.length + 1);
            existingValues[existingValues.length - 1] = value;
        } else {
            // 如果键不存在，则创建一个新的数组并存储值
            existingValues = new String[]{value};
        }

        // 将更新后的数组重新放入参数映射中
        parameterMap.put(key, existingValues);
    }

    /**
     * 对给定的字符串进行URL解码。
     * <p>
     * 该方法首先检查输入值是否为null，如果为null则直接返回null。否则，尝试使用指定的字符编码对字符串进行URL解码。
     * 如果解码过程中发生UnsupportedEncodingException异常，则记录警告日志并返回原始值。
     *
     * @param value 需要进行URL解码的字符串。如果为null，则直接返回null。
     * @return 解码后的字符串。如果解码失败，则返回原始值。
     */
    private String decode(String value) {
        // 如果输入值为null，直接返回null
        if (value == null) {
            return null;
        }

        try {
            // 使用指定的字符编码对字符串进行URL解码
            return URLDecoder.decode(value, getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            // 如果解码失败，记录警告日志并返回原始值
            log.warn("Failed to decode value: {}. Encoding: {}", value, getCharacterEncoding(), e);
            return value;
        }
    }

    /**
     * 获取请求体字符串
     * <p>
     * 该方法从HttpServletRequest对象中读取请求体内容，并将其转换为字符串返回。
     * 如果读取过程中发生IOException，则记录错误日志并返回空字符串。
     *
     * @param request HttpServletRequest对象，表示HTTP请求
     * @return 请求体内容的字符串表示，如果读取失败则返回空字符串
     */
    private String getBodyString(HttpServletRequest request) {
        // 使用try-with-resources语句确保ServletInputStream在使用后自动关闭
        try (ServletInputStream inputStream = request.getInputStream()) {
            // 使用IoUtil工具类从输入流中读取数据，并将其转换为指定字符编码的字符串
            return IoUtil.read(inputStream, CharsetUtil.charset(getCharacterEncoding()));
        } catch (IOException e) {
            // 如果读取请求体时发生异常，记录错误日志并返回空字符串
            log.error("Failed to read request body. Request: {}", request.getRequestURI(), e);
            return "";
        }
    }

    /**
     * 重写父类的getInputStream方法，返回一个ServletInputStream对象。
     * 该方法将当前对象的body内容转换为字节数组，并使用指定的字符编码进行编码。
     * 返回的ServletInputStream对象可以用于读取body的内容。
     *
     * @return ServletInputStream 一个包含body内容的ServletInputStream对象
     * @throws IOException 如果发生I/O错误，则抛出IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 将body内容转换为字节数组，并使用指定的字符编码进行编码，然后返回一个ByteArrayServletInputStream对象
        return new ByteArrayServletInputStream(body.getBytes(getCharacterEncoding()));
    }

    /**
     * 获取一个用于读取数据的BufferedReader对象。
     * 该方法通过调用getInputStream()获取输入流，并使用getCharacterEncoding()获取字符编码，
     * 将输入流包装为InputStreamReader，最后再包装为BufferedReader返回。
     *
     * @return 返回一个BufferedReader对象，用于从输入流中读取字符数据。
     * @throws IOException 如果在获取输入流或创建BufferedReader时发生I/O错误，则抛出此异常。
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    /**
     * 获取指定名称的请求参数值。
     * 如果存在多个同名参数，则返回第一个参数的值。
     * 如果未找到指定名称的参数，则调用父类的getParameter方法获取默认值。
     *
     * @param name 请求参数的名称
     * @return 返回指定名称的请求参数值。如果未找到，则返回父类方法的结果。
     */
    @Override
    public String getParameter(String name) {
        // 获取指定名称的所有参数值
        String[] values = getParameterValues(name);

        // 如果存在参数值且数组不为空，则返回第一个值；否则调用父类方法获取默认值
        return values != null && values.length > 0 ? values[0] : super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap.isEmpty() ? super.getParameterMap() : parameterMap;
    }

    /**
     * 获取请求参数名称的枚举。
     * <p>
     * 该方法用于返回当前请求中所有参数名称的枚举。如果当前请求没有参数（即parameterMap为空），
     * 则调用父类的getParameterNames方法获取参数名称的枚举；否则，返回parameterMap中所有键的枚举。
     *
     * @return 包含所有参数名称的枚举对象。如果parameterMap为空，则返回父类的参数名称枚举；
     * 否则，返回parameterMap的键集合的枚举。
     */
    @Override
    public Enumeration<String> getParameterNames() {
        // 如果parameterMap为空，调用父类的getParameterNames方法
        // 否则，返回parameterMap的键集合的枚举
        return parameterMap.isEmpty() ? super.getParameterNames() : Collections.enumeration(parameterMap.keySet());
    }

    /**
     * 根据参数名称获取对应的参数值数组。
     * 该方法首先从当前对象的parameterMap中查找指定名称的参数值数组，
     * 如果parameterMap中不存在该名称的参数，则调用父类的getParameterValues方法获取参数值数组。
     *
     * @param name 参数名称，用于查找对应的参数值数组
     * @return 返回与指定名称对应的参数值数组。如果parameterMap中不存在该名称的参数，则返回父类方法的结果。
     */
    @Override
    public String[] getParameterValues(String name) {
        // 从parameterMap中获取指定名称的参数值数组，如果不存在则调用父类方法
        return parameterMap.getOrDefault(name, super.getParameterValues(name));
    }

}
