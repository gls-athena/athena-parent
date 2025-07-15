package com.gls.athena.starter.aliyun.oss.support;

import com.gls.athena.starter.aliyun.oss.service.OssClientService;
import com.gls.athena.starter.aliyun.oss.service.OssMetadataService;
import com.gls.athena.starter.aliyun.oss.service.OssStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OSS 资源工厂，负责创建和验证 OSS 资源。
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
     * 创建 OSS 资源。
     *
     * @param location OSS 资源位置
     * @return OSS 资源对象
     * @throws IllegalArgumentException 当 URI 格式不正确时
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
