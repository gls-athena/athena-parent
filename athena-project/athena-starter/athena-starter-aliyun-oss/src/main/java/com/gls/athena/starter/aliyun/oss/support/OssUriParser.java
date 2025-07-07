package com.gls.athena.starter.aliyun.oss.support;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * OSS URI 解析器，负责解析和验证 OSS URI 格式。
 *
 * <p>支持的URI格式：
 * <ul>
 *     <li>oss://bucketName/objectKey - 访问具体对象</li>
 *     <li>oss://bucketName - 访问存储空间</li>
 * </ul>
 *
 * @author george
 */
@Slf4j
@Getter
public class OssUriParser {

    public static final String OSS_PROTOCOL = "oss";
    private static final String OSS_SCHEME_PREFIX = "oss://";

    private final URI uri;
    private final String bucketName;
    private final String objectKey;
    private final boolean isBucket;

    /**
     * 构造函数，解析 OSS URI。
     *
     * @param location OSS 资源位置
     * @throws IllegalArgumentException 当 URI 格式不正确时
     */
    public OssUriParser(String location) {
        if (!isValidOssUri(location)) {
            throw new IllegalArgumentException("无效的 OSS URI 格式: " + location);
        }

        this.uri = URI.create(location);
        this.bucketName = extractBucketName();
        this.objectKey = extractObjectKey();
        this.isBucket = StrUtil.isEmpty(objectKey);

        log.debug("解析 OSS URI: bucket={}, objectKey={}, isBucket={}", bucketName, objectKey, isBucket);
    }

    /**
     * 验证是否为有效的 OSS URI。
     *
     * @param location URI 字符串
     * @return true 如果是有效的 OSS URI
     */
    public static boolean isValidOssUri(String location) {
        if (StrUtil.isEmpty(location) || !location.startsWith(OSS_SCHEME_PREFIX)) {
            return false;
        }

        try {
            URI uri = URI.create(location);
            return OSS_PROTOCOL.equals(uri.getScheme()) && StrUtil.isNotEmpty(uri.getAuthority());
        } catch (Exception e) {
            log.warn("URI 解析失败: {}", location, e);
            return false;
        }
    }

    private String extractBucketName() {
        String authority = uri.getAuthority();
        if (StrUtil.isEmpty(authority)) {
            throw new IllegalArgumentException("OSS URI 中缺少 bucket 名称");
        }
        return authority;
    }

    private String extractObjectKey() {
        String path = uri.getPath();
        if (StrUtil.isEmpty(path) || "/".equals(path)) {
            return "";
        }
        return path.startsWith("/") ? path.substring(1) : path;
    }
}
