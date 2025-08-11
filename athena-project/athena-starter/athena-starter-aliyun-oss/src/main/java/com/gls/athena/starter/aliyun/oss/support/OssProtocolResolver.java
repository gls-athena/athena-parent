package com.gls.athena.starter.aliyun.oss.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * OSS 协议解析器，用于处理以 "oss://" 开头的资源路径。
 * <p>
 * 主要职责：
 * <ul>
 *   <li>识别并处理 OSS 协议资源路径</li>
 *   <li>将解析任务委托给 OssResourceFactory</li>
 *   <li>注册自身到 ResourceLoader 以支持自定义协议</li>
 * </ul>
 *
 * @author george
 */
@Slf4j
@RequiredArgsConstructor
public class OssProtocolResolver implements ProtocolResolver, ResourceLoaderAware {

    private final OssResourceFactory ossResourceFactory;

    /**
     * 设置资源加载器，并将当前实例注册为协议解析器。
     *
     * @param resourceLoader 资源加载器，必须是DefaultResourceLoader实例
     * @throws IllegalArgumentException 当传入不支持的ResourceLoader类型时抛出
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        // 检查资源加载器是否为空
        Assert.notNull(resourceLoader, "ResourceLoader不能为空");

        // 判断资源加载器类型并进行相应处理
        if (resourceLoader instanceof DefaultResourceLoader defaultResourceLoader) {
            // 将当前实例注册为协议解析器
            defaultResourceLoader.addProtocolResolver(this);
            log.debug("成功注册OSS协议解析器");
        } else {
            // 不支持的资源加载器类型，记录错误日志并抛出异常
            String errorMessage = String.format("不支持的资源加载器类型: %s",
                    resourceLoader.getClass().getName());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * 解析指定位置的OSS资源并封装为OssResource对象。
     *
     * <p>此方法专注于协议识别，具体的资源创建委托给 OssResourceFactory。
     *
     * @param location       资源定位符，需要以"oss://"开头
     * @param resourceLoader 资源加载器（本实现中未直接使用）
     * @return 封装后的OSS资源对象，当location不符合要求时返回null
     */
    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        // 增加空值检查，防止后续调用抛出 NPE
        if (location == null) {
            log.warn("传入的资源路径为 null");
            return null;
        }

        // 快速检查是否为 OSS 协议
        if (!ossResourceFactory.isValidOssLocation(location)) {
            return null;
        }

        log.trace("正在解析OSS资源: {}", location); // 使用 trace 级别减少调试日志输出压力
        try {
            return ossResourceFactory.createResource(location);
        } catch (Exception e) {
            log.error("解析OSS资源失败: {}", location, e);
            return null;
        }
    }

}
