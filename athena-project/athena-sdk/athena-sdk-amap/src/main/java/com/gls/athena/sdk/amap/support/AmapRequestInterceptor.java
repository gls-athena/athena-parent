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
    private final AmapProperties properties;

    /**
     * 拦截并处理请求
     * 主要完成以下三个任务：
     * 1. 添加API密钥(key)
     * 2. 生成并添加数字签名(sig)
     * 3. 设置API版本和请求目标地址
     *
     * @param template Feign请求模板，包含请求的相关信息，如URL、请求方法、请求头等。
     */
    @Override
    public void apply(RequestTemplate template) {
        // 验证必要的属性是否已正确设置
        validateProperties();

        // 在请求模板中添加API密钥
        setKey(template);

        // 生成数字签名并将其添加到请求模板中
        setSig(template);

        // 设置API版本和请求的目标地址
        setVersionAndTarget(template);
    }

    /**
     * 验证配置属性
     * <p>
     * 该方法用于检查配置属性是否完整，主要验证高德地图的host和key是否已配置。
     * 如果任一必要属性缺失，将抛出IllegalArgumentException异常。
     *
     * @throws IllegalArgumentException 当必要的配置属性缺失时抛出
     */
    private void validateProperties() {
        // 检查高德地图的host是否配置
        if (StrUtil.isBlank(properties.getHost())) {
            throw new IllegalArgumentException("高德地图host未配置");
        }
        // 检查高德地图的key是否配置
        if (StrUtil.isBlank(properties.getKey())) {
            throw new IllegalArgumentException("高德地图key未配置");
        }
    }

    /**
     * 设置API版本和请求目标地址
     * <p>
     * 该函数的主要功能是为Feign请求设置API版本和构建完整的请求URL。具体步骤如下：
     * 1. 获取API版本，默认使用v3版本或通过注解指定。
     * 2. 根据API版本和请求模板构建完整的请求URL。
     * 3. 将构建好的目标URL设置到Feign请求模板中。
     *
     * @param template Feign请求模板，包含请求的基本信息，用于构建目标URL。
     */
    private void setVersionAndTarget(RequestTemplate template) {
        // 获取API版本，默认v3或通过注解指定
        String version = getApiVersion(template);

        // 根据API版本和请求模板构建完整的请求URL
        String target = buildTargetUrl(template, version);

        // 记录调试信息，输出构建的目标URL
        log.debug("AmapRequestInterceptor target: {}", target);

        // 将构建好的目标URL设置到Feign请求模板中
        template.target(target);
    }

    /**
     * 获取API版本
     * <p>
     * 该方法通过解析Feign请求模板中的方法元数据，获取方法上的`AmapVersion`注解，并返回注解中指定的API版本。
     * 如果方法上没有`AmapVersion`注解，则返回默认的API版本。
     *
     * @param template Feign请求模板，包含请求的方法元数据等信息
     * @return API版本，如果方法上有`AmapVersion`注解，则返回注解中指定的版本；否则返回默认版本
     */
    private String getApiVersion(RequestTemplate template) {
        // 从请求模板中获取方法元数据
        Method method = template.methodMetadata().method();

        // 获取方法上的`AmapVersion`注解，并返回注解中的版本值，如果注解不存在则返回默认版本
        return Optional.ofNullable(method.getAnnotation(AmapVersion.class))
                .map(AmapVersion::value)
                .orElse(DEFAULT_VERSION);
    }

    /**
     * 构建目标URL
     * <p>
     * 该方法根据Feign请求模板和API版本，构建完整的目标URL。首先从Feign请求模板中提取原始URL，
     * 然后解析该URL的路径部分，最后将主机地址、API版本和路径拼接成完整的目标URL。
     *
     * @param template Feign请求模板，包含目标服务的基本信息
     * @param version  API版本，用于构建URL中的版本路径
     * @return 完整的目标URL，包含主机地址、API版本和路径
     */
    private String buildTargetUrl(RequestTemplate template, String version) {
        // 从Feign请求模板中获取目标服务的原始URL
        String urlStr = template.feignTarget().url();

        // 将原始URL解析为URI对象，以便提取路径部分
        URI uri = URI.create(urlStr);
        String path = uri.getPath();

        // 拼接主机地址、API版本和路径，生成完整的目标URL
        return properties.getHost() + "/" + version + path;
    }

    /**
     * 设置数字签名
     * <p>
     * 当配置了privateKey时，根据请求参数生成MD5签名，并将签名添加到请求的查询参数中。
     * 如果privateKey为空或未配置，则不进行任何操作。
     *
     * @param template Feign请求模板，包含请求的查询参数等信息
     */
    private void setSig(RequestTemplate template) {
        // 获取私钥配置
        String privateKey = properties.getPrivateKey();
        // 只有当私钥不为空时才进行签名处理
        if (StrUtil.isNotBlank(privateKey)) {
            // 生成签名
            String sig = getSig(template.queries(), privateKey);
            // 将生成的签名添加到查询参数中
            if (sig != null) {
                log.debug("AmapRequestInterceptor sig: {}", sig);
                template.query("sig", sig);
            }
        }
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
     * @param queries    请求参数，包含多个键值对，每个键对应一个字符串集合
     * @param privateKey 私钥，用于生成签名的附加字符串
     * @return MD5加密后的签名，作为字符串返回
     */
    private String getSig(Map<String, Collection<String>> queries, String privateKey) {
        // 使用StringBuilder提高字符串拼接性能，预估容量减少扩容
        StringBuilder paramBuilder = new StringBuilder(256);

        // 将请求参数按照key升序排序，并对每个value进行URL解码，然后拼接成key=value的形式
        queries.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String key = entry.getKey();
                    entry.getValue().stream()
                            .sorted()
                            .forEach(value -> {
                                if (!paramBuilder.isEmpty()) {
                                    paramBuilder.append("&");
                                }
                                paramBuilder.append(key)
                                        .append("=")
                                        .append(URLDecoder.decode(value, StandardCharsets.UTF_8));
                            });
                });

        // 将拼接后的参数字符串与私钥拼接，生成最终地签名字符串
        String signStr = paramBuilder.append(privateKey).toString();
        log.debug("AmapRequestInterceptor params: {}", signStr);

        // 对最终地签名字符串进行MD5加密，并返回加密后的结果
        return SecureUtil.md5(signStr);
    }

    /**
     * 设置API密钥
     * 将配置的API密钥添加到Feign请求模板的查询参数中。
     *
     * @param template Feign请求模板，用于构建HTTP请求。该模板将被修改以包含API密钥作为查询参数。
     */
    private void setKey(RequestTemplate template) {
        // 将配置的API密钥添加到请求模板的查询参数中
        template.query("key", properties.getKey());
    }

}
