package com.gls.athena.sdk.amap.support;

import com.gls.athena.sdk.amap.config.AmapProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * 高德请求拦截器
 *
 * @author george
 */
@RequiredArgsConstructor
public class AmapRequestInterceptor implements RequestInterceptor {
    /**
     * 高德配置
     */
    private final AmapProperties properties;

    /**
     * 应用
     *
     * @param template 模板
     */
    @Override
    public void apply(RequestTemplate template) {
        // 如果没有设置key，则使用默认key
        if (properties.getKey() != null) {
            template.query("key", properties.getKey());
        }
        // 设置默认版本
        String version = "v3";
        Method method = template.methodMetadata().method();
        if (method.isAnnotationPresent(AmapVersion.class)) {
            version = method.getAnnotation(AmapVersion.class).value();
        }
        // 设置请求目标和相对路径
        String urlStr = template.feignTarget().url();
        URI uri = URI.create(urlStr);
        String path = uri.getPath();
        String target = properties.getHost() + "/" + version + path;
        // 设置请求目标和相对路径
        template.target(target);
    }
}
