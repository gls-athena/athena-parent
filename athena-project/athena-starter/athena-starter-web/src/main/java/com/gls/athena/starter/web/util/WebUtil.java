package com.gls.athena.starter.web.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

/**
 * web工具类
 *
 * @author george
 */
@UtilityClass
public class WebUtil {

    /**
     * 获取请求参数
     *
     * @param request 请求
     * @return 请求参数
     */
    public MultiValueMap<String, String> getParameterMap(HttpServletRequest request) {
        // 创建参数映射
        MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>();
        // 遍历请求参数
        request.getParameterMap().forEach((key, value) -> parameterMap.put(key, CollUtil.newArrayList(value)));
        // 返回参数映射
        return parameterMap;
    }

    /**
     * 获取请求
     *
     * @return 请求
     */
    public Optional<HttpServletRequest> getRequest() {
        // 获取请求属性
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 如果是Servlet请求属性
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            // 返回请求
            return Optional.of(servletRequestAttributes.getRequest());
        }
        // 返回空
        return Optional.empty();
    }

    /**
     * 获取响应
     *
     * @return 响应
     */
    public Optional<HttpServletResponse> getResponse() {
        // 获取请求属性
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 如果是Servlet请求属性
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            // 返回响应
            return Optional.ofNullable(servletRequestAttributes.getResponse());
        }
        // 返回空
        return Optional.empty();
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
