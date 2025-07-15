package com.gls.athena.starter.aliyun.oss.support;

import com.gls.athena.starter.aliyun.oss.service.OssClientService;
import com.gls.athena.starter.aliyun.oss.service.OssMetadataService;
import com.gls.athena.starter.aliyun.oss.service.OssStreamService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OssResourceFactory {

    private final OssClientService ossClientService;
    private final OssStreamService ossStreamService;
    private final OssMetadataService ossMetadataService;

    /**
     * 创建 OSS 资源对象。
     * <p>
     * 根据传入的 OSS 资源位置字符串，解析为 URI，并结合 OSS 客户端、流服务、元数据服务，
     * 构建出可操作的 OSS 资源对象。若 location 格式非法，则抛出异常。
     * </p>
     *
     * @param location OSS 资源位置字符串（如 oss://bucket/key）
     * @return OSS 资源对象
     * @throws IllegalArgumentException 当 location 格式不正确时抛出
     */
    public OssResource createResource(String location) {
        OssUriParser uriParser = new OssUriParser(location);
        return new OssResource(uriParser, ossClientService, ossStreamService, ossMetadataService);
    }

    /**
     * 验证是否为有效的 OSS 资源位置。
     *
     * @param location 资源位置
     * @return true 如果是有效的 OSS 资源位置
     */
    public boolean isValidOssLocation(String location) {
        return OssUriParser.isValidOssUri(location);
    }
}
