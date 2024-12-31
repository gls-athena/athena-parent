package com.gls.athena.sdk.amap.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.gls.athena.sdk.amap.config.AmapProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * 高德请求拦截器
 *
 * @author george
 */
@Slf4j
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
        // 设置密钥
        setKey(template);
        // 设置数字签名
        setSig(template);
        // 设置版本和请求目标
        setVersionAndTarget(template);
    }

    /**
     * 设置版本和请求目标
     *
     * @param template 模板
     */
    private void setVersionAndTarget(RequestTemplate template) {
        if (StrUtil.isBlank(properties.getHost())) {
            throw new IllegalArgumentException("高德地图host未配置");
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
        log.debug("AmapRequestInterceptor target: {}", target);
        // 设置请求目标和相对路径
        template.target(target);
    }

    /**
     * 设置数字签名
     *
     * @param template 模板
     */
    private void setSig(RequestTemplate template) {
        String privateKey = properties.getPrivateKey();
        if (StrUtil.isBlank(privateKey)) {
            return;
        }
        String sig = getSig(template.queries(), privateKey);
        log.debug("AmapRequestInterceptor sig: {}", sig);
        template.query("sig", sig);
    }

    /**
     * 获取数字签名
     *
     * @param queries    查询参数
     * @param privateKey 私钥
     * @return 数字签名
     */
    private String getSig(Map<String, Collection<String>> queries, String privateKey) {
        StringBuilder sb = new StringBuilder();
        // 参数排序
        queries.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String key = entry.getKey();
                    Collection<String> values = entry.getValue();
                    values.stream()
                            .sorted()
                            .forEach(value -> {
                                // 解码
                                String decodeValue = URLDecoder.decode(value, StandardCharsets.UTF_8);
                                sb.append(key).append("=").append(decodeValue).append("&");
                            });
                });
        // 删除最后一个 &
        sb.deleteCharAt(sb.length() - 1);
        // 拼接私钥
        sb.append(privateKey);
        // 参数拼接
        String params = sb.toString();
        log.debug("AmapRequestInterceptor params: {}", params);
        return SecureUtil.md5(params);
    }

    /**
     * 设置密钥
     *
     * @param template 模板
     */
    private void setKey(RequestTemplate template) {
        if (StrUtil.isBlank(properties.getKey())) {
            throw new IllegalArgumentException("高德地图key未配置");
        }
        template.query("key", properties.getKey());
    }
}
