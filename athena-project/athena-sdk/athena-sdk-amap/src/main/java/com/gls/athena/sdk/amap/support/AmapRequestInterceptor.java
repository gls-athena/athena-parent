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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 高德地图API请求拦截器
 * 用于处理请求参数的预处理，包括添加密钥、数字签名等安全认证信息
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class AmapRequestInterceptor implements RequestInterceptor {
    /**
     * 默认API版本
     */
    private static final String DEFAULT_VERSION = "v3";

    /**
     * 高德地图API配置属性
     * 包含key、privateKey、host等配置信息
     */
    private final AmapProperties properties;

    /**
     * 拦截并处理请求
     * 主要完成以下三个任务：
     * 1. 添加API密钥(key)
     * 2. 生成并添加数字签名(sig)
     * 3. 设置API版本和请求目标地址
     *
     * @param template Feign请求模板
     */
    @Override
    public void apply(RequestTemplate template) {
        validateProperties();
        setKey(template);
        setSig(template);
        setVersionAndTarget(template);
    }

    /**
     * 验证配置属性
     *
     * @throws IllegalArgumentException 当必要的配置属性缺失时抛出
     */
    private void validateProperties() {
        if (StrUtil.isBlank(properties.getHost())) {
            throw new IllegalArgumentException("高德地图host未配置");
        }
        if (StrUtil.isBlank(properties.getKey())) {
            throw new IllegalArgumentException("高德地图key未配置");
        }
    }

    /**
     * 设置API版本和请求目标地址
     * 1. 获取API版本（默认v3或通过注解指定）
     * 2. 构建完整的请求URL
     *
     * @param template Feign请求模板
     */
    private void setVersionAndTarget(RequestTemplate template) {
        String version = getApiVersion(template);
        String target = buildTargetUrl(template, version);
        log.debug("AmapRequestInterceptor target: {}", target);
        template.target(target);
    }

    /**
     * 获取API版本
     *
     * @param template Feign请求模板
     * @return API版本
     */
    private String getApiVersion(RequestTemplate template) {
        Method method = template.methodMetadata().method();
        return Optional.ofNullable(method.getAnnotation(AmapVersion.class))
                .map(AmapVersion::value)
                .orElse(DEFAULT_VERSION);
    }

    /**
     * 构建目标URL
     *
     * @param template Feign请求模板
     * @param version  API版本
     * @return 完整的目标URL
     */
    private String buildTargetUrl(RequestTemplate template, String version) {
        String urlStr = template.feignTarget().url();
        URI uri = URI.create(urlStr);
        String path = uri.getPath();
        return properties.getHost() + "/" + version + path;
    }

    /**
     * 设置数字签名
     * 当配置了privateKey时，根据请求参数生成MD5签名
     *
     * @param template Feign请求模板
     */
    private void setSig(RequestTemplate template) {
        Optional.ofNullable(properties.getPrivateKey())
                .filter(StrUtil::isNotBlank)
                .map(privateKey -> getSig(template.queries(), privateKey))
                .ifPresent(sig -> {
                    log.debug("AmapRequestInterceptor sig: {}", sig);
                    template.query("sig", sig);
                });
    }

    /**
     * 生成数字签名
     * 签名规则：
     * 1. 对请求参数按照key升序排序
     * 2. 对value进行URL解码
     * 3. 将所有参数以key=value形式拼接，并以&连接
     * 4. 拼接私钥
     * 5. 对最终字符串进行MD5加密
     *
     * @param queries    请求参数
     * @param privateKey 私钥
     * @return MD5加密后的签名
     */
    private String getSig(Map<String, Collection<String>> queries, String privateKey) {
        String params = queries.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().stream()
                        .sorted()
                        .map(value -> entry.getKey() + "=" + URLDecoder.decode(value, StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));

        String signStr = params + privateKey;
        log.debug("AmapRequestInterceptor params: {}", signStr);
        return SecureUtil.md5(signStr);
    }

    /**
     * 设置API密钥
     * 将配置的key添加到请求参数中
     *
     * @param template Feign请求模板
     */
    private void setKey(RequestTemplate template) {
        template.query("key", properties.getKey());
    }
}
