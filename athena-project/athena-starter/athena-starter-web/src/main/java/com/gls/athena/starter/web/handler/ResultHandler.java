package com.gls.athena.starter.web.handler;

import cn.hutool.json.JSONUtil;
import com.gls.athena.common.bean.result.Result;
import com.gls.athena.common.bean.result.ResultStatus;
import com.gls.athena.common.core.constant.IConstants;
import com.gls.athena.starter.web.config.IWebConstants;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 结果通知
 *
 * @author george
 */
@RestControllerAdvice(basePackages = IConstants.BASE_PACKAGE_PREFIX)
public class ResultHandler implements ResponseBodyAdvice<Object> {
    /**
     * 是否支持
     *
     * @param returnType    返回类型
     * @param converterType 转换器类型
     * @return 是否支持
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果是返回值是Result类型 直接返回
        if (Result.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        // 如果方法上有@ResultIgnore注解 直接返回
        if (returnType.getMethod().isAnnotationPresent(ResultIgnore.class)
                || returnType.getDeclaringClass().isAnnotationPresent(ResultIgnore.class)) {
            return false;
        }
        return true;
    }

    /**
     * 在写入响应体之前调用
     *
     * @param body                  返回值
     * @param returnType            返回类型
     * @param selectedContentType   上下文类型
     * @param selectedConverterType 转换器类型
     * @param request               请求
     * @param response              响应
     * @return 返回值
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 判断客户端类型 是否是feign调用
        if (Objects.equals(request.getHeaders().getFirst(IWebConstants.CLIENT_TYPE), IWebConstants.CLIENT_TYPE_FEIGN)) {
            return body;
        }
        // 判断返回值是否是字符串
        if (body instanceof String str) {
            return JSONUtil.toJsonStr(ResultStatus.SUCCESS.toResult(str));
        }
        return ResultStatus.SUCCESS.toResult(body);
    }
}
