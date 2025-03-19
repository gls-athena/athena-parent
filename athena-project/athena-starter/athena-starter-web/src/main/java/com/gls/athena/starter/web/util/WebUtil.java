package com.gls.athena.starter.web.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * web工具类
 *
 * @author george
 */
@UtilityClass
public class WebUtil {

    /**
     * 将HttpServletRequest中的请求参数转换为MultiValueMap结构
     *
     * @param request HttpServletRequest对象，包含客户端请求的参数
     * @return LinkedMultiValueMap<String, String> 保持参数顺序的MultiValueMap结构，
     * 其中键为参数名，值为参数值列表（支持多值参数）
     * <p>
     * 实现逻辑：
     * 1. 通过request.getParameterMap()获取原始参数映射
     * 2. 将每个参数值数组转换为ArrayList，以适配MultiValueMap的值类型要求
     * 3. 使用Linked结构保持参数原始顺序
     */
    public MultiValueMap<String, String> getParameterMap(HttpServletRequest request) {
        // 创建保持插入顺序的MultiValueMap实例
        MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>();

        // 遍历处理所有请求参数：将String[]转换为List<String>
        // 兼容Servlet规范中参数多值的存储格式
        request.getParameterMap().forEach((key, values) ->
                parameterMap.put(key, new ArrayList<>(Arrays.asList(values)))
        );

        return parameterMap;
    }

    /**
     * 获取请求
     *
     * @return 请求
     */
    public Optional<HttpServletRequest> getRequest() {
        return Optional.of(RequestContextHolder.currentRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * 获取响应
     *
     * @return 响应
     */
    public Optional<HttpServletResponse> getResponse() {
        return Optional.of(RequestContextHolder.currentRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getResponse);
    }

    /**
     * 获取请求参数
     *
     * @param request       请求
     * @param parameterName 参数名称
     * @return 请求参数
     */
    public String getParameter(HttpServletRequest request, String parameterName) {
        // 获取请求参数
        String parameter = WebUtils.findParameterValue(request, parameterName);
        // 如果请求参数不为空
        if (StrUtil.isNotBlank(parameter)) {
            // 返回请求参数
            return parameter;
        }
        // 返回请求体中的参数
        return getParameterByBody(request, parameterName);
    }

    /**
     * 获取请求体中的参数
     *
     * @param request       请求
     * @param parameterName 参数名称
     * @return 请求参数
     */
    public String getParameterByBody(HttpServletRequest request, String parameterName) {
        // 获取请求体
        String body = JakartaServletUtil.getBody(request);
        // 如果请求体不为空
        if (StrUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
            // 返回请求体中的参数
            return JSONUtil.parseObj(body).getStr(parameterName);
        }
        // 返回空
        return null;
    }
}
