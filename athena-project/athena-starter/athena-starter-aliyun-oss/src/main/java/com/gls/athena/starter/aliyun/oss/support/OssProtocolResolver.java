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
 * OSS协议解析器
 * 用于解析oss://开头的资源路径
 *
 * @author george
 */
@Slf4j
public class OssProtocolResolver implements ProtocolResolver, ResourceLoaderAware {

    public static final String PROTOCOL = "oss://";
    private static final String ERROR_UNSUPPORTED_RESOURCE_LOADER = "不支持的资源加载器类型: %s";

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

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (!StringUtils.hasText(location) || !location.startsWith(PROTOCOL)) {
            return null;
        }

        log.debug("正在解析OSS资源: {}", location);
        return new OssResource(location);
    }
}
