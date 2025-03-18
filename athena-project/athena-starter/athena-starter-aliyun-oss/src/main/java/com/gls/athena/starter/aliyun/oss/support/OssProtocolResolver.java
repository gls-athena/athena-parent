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

    /**
     * 设置资源加载器，并将当前实例注册为协议解析器
     *
     * @param resourceLoader 要设置的资源加载器，必须是DefaultResourceLoader实例
     * @throws IllegalArgumentException 当传入不支持的ResourceLoader类型时抛出
     *                                  <p>
     *                                  方法逻辑说明：
     *                                  1. 参数有效性检查确保resourceLoader不为空
     *                                  2. 仅支持DefaultResourceLoader类型的资源加载器：
     *                                  - 将当前实例注册为协议解析器
     *                                  - 记录协议解析器注册成功的调试日志
     *                                  3. 当传入不支持的ResourceLoader类型时，抛出带详细错误信息的异常
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        // 参数空值检查：确保传入有效的资源加载器
        Assert.notNull(resourceLoader, "ResourceLoader不能为空");

        // 仅处理DefaultResourceLoader及其子类实例
        if (resourceLoader instanceof DefaultResourceLoader) {
            // 注册当前实例作为协议解析器
            ((DefaultResourceLoader) resourceLoader).addProtocolResolver(this);
            // 记录协议解析器注册成功的调试信息
            log.debug("成功注册OSS协议解析器");
        } else {
            // 抛出明确异常说明不支持的资源加载器类型
            throw new IllegalArgumentException(String.format(ERROR_UNSUPPORTED_RESOURCE_LOADER,
                    resourceLoader.getClass().getName()));
        }
    }

    /**
     * 解析指定位置的OSS资源并封装为OssResource对象
     *
     * @param location       资源定位符，需要以指定协议前缀开头
     * @param resourceLoader 资源加载器（本实现中未直接使用）
     * @return 封装后的OSS资源对象，当location不符合要求时返回null
     * <p>
     * 处理逻辑：
     * 1. 校验location有效性：非空且包含协议前缀
     * 2. 记录资源解析日志
     * 3. 创建OSS资源封装对象
     */
    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        // 校验location格式是否符合OSS资源协议要求
        if (!StringUtils.hasText(location) || !location.startsWith(PROTOCOL)) {
            return null;
        }

        // 处理有效的OSS资源定位符
        log.debug("正在解析OSS资源: {}", location);
        return new OssResource(location);
    }

}
