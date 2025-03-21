package com.gls.athena.starter.aliyun.oss.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * OSS协议解析器，用于解析以"oss://"开头的资源路径。
 *
 * @author george
 */
@Slf4j
public class OssProtocolResolver implements ProtocolResolver, ResourceLoaderAware {

    public static final String PROTOCOL = "oss://";
    private static final String ERROR_UNSUPPORTED_RESOURCE_LOADER = "不支持的资源加载器类型: %s";

    /**
     * 设置资源加载器，并将当前实例注册为协议解析器。
     *
     * @param resourceLoader 资源加载器，必须是DefaultResourceLoader实例
     * @throws IllegalArgumentException 当传入不支持的ResourceLoader类型时抛出
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader不能为空");

        if (resourceLoader instanceof DefaultResourceLoader) {
            ((DefaultResourceLoader) resourceLoader).addProtocolResolver(this);
            log.debug("成功注册OSS协议解析器");
        } else {
            throw new IllegalArgumentException(String.format(ERROR_UNSUPPORTED_RESOURCE_LOADER,
                    resourceLoader.getClass().getName()));
        }
    }

    /**
     * 解析指定位置的OSS资源并封装为OssResource对象。
     *
     * @param location       资源定位符，需要以"oss://"开头
     * @param resourceLoader 资源加载器（本实现中未直接使用）
     * @return 封装后的OSS资源对象，当location不符合要求时返回null
     */
    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (!StringUtils.hasText(location) || !location.startsWith(PROTOCOL)) {
            return null;
        }

        log.debug("正在解析OSS资源: {}", location);
        try {
            return new OssResource(location);
        } catch (Exception e) {
            log.error("解析OSS资源失败: {}", location, e);
            return null;
        }
    }
}
