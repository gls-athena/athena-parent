package com.gls.athena.starter.aliyun.oss.support;

import com.gls.athena.starter.aliyun.oss.service.OssService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OSS 资源工厂，负责创建和验证 OSS 资源。
 * <p>
 * 该工厂通过注入的 OSS 客户端、流服务和元数据服务，
 * 提供统一的 OSS 资源创建入口，并对资源位置进行格式校验。
 * </p>
 *
 * @author george
 */
@Slf4j
@Component
public class OssResourceFactory {

    @Resource
    private OssService ossService;

    /**
     * 创建 OSS 资源对象。
     * <p>
     * 根据传入的 OSS 资源位置字符串，解析为 URI，并结合 OSS 客户端、流服务、元数据服务，
     * 构建出可操作的 OSS 资源对象。若 location 格式非法，则抛出异常。
     * </p>
     *
     * @param location OSS 资源位置字符串（如 oss://bucket/key）
     * @return OSS 资源对象
     * @throws IllegalArgumentException 当 location 格式不正确或为 null 时抛出
     */
    public OssResource createResource(String location) {
        // 参数校验：检查 location 是否为 null
        if (location == null) {
            throw new IllegalArgumentException("Location must not be null");
        }

        // 解析 OSS URI 并创建资源对象
        try {
            OssUriParser uriParser = new OssUriParser(location);
            return new OssResource(uriParser, ossService);
        } catch (Exception e) {
            log.warn("Failed to parse OSS URI: {}", location, e);
            throw new IllegalArgumentException("Invalid OSS location: " + location, e);
        }
    }

    /**
     * 验证是否为有效的 OSS 资源位置。
     *
     * @param location 资源位置
     * @return true 如果是有效的 OSS 资源位置
     */
    public boolean isValidOssLocation(String location) {
        // 检查输入参数是否为 null
        if (location == null) {
            return false;
        }
        try {
            // 使用 OssUriParser 验证 OSS URI 的有效性
            return OssUriParser.isValidOssUri(location);
        } catch (Exception e) {
            // 记录无效的 OSS URI 信息用于调试
            log.debug("Invalid OSS URI detected: {}", location, e);
            return false;
        }
    }

}
