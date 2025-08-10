package com.gls.athena.starter.aliyun.oss.support;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * OSS URI 解析器，用于解析和校验 OSS URI 格式。
 * <p>
 * 支持的 URI 格式：
 * <ul>
 *   <li><b>oss://bucketName/objectKey</b> —— 访问具体对象</li>
 *   <li><b>oss://bucketName</b> —— 访问存储空间</li>
 * </ul>
 * <p>
 * 示例：
 * <ul>
 *   <li>oss://my-bucket/path/to/file.txt</li>
 *   <li>oss://my-bucket</li>
 * </ul>
 * <p>
 * 该类会自动识别 bucket 名称和 object key，并判断 URI 是否仅为 bucket。
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
        if (StrUtil.isEmpty(location)) {
            throw new IllegalArgumentException("OSS URI 不能为空");
        }

        try {
            this.uri = URI.create(location);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的 OSS URI 格式: " + location, e);
        }

        if (!OSS_PROTOCOL.equals(this.uri.getScheme())) {
            throw new IllegalArgumentException("OSS URI 必须以 oss:// 开头: " + location);
        }

        String authority = this.uri.getAuthority();
        if (StrUtil.isEmpty(authority)) {
            throw new IllegalArgumentException("OSS URI 中缺少 bucket 名称: " + location);
        }
        this.bucketName = authority;

        String path = this.uri.getPath();
        if (StrUtil.isEmpty(path) || "/".equals(path)) {
            this.objectKey = "";
        } else {
            this.objectKey = path.startsWith("/") ? path.substring(1) : path;
        }

        this.isBucket = StrUtil.isEmpty(this.objectKey);

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
}
