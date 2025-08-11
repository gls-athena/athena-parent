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
     * @param location OSS 资源位置，必须为 oss:// 开头的有效 URI
     * @throws IllegalArgumentException 当 URI 格式不正确或参数非法时抛出异常
     */
    public OssUriParser(String location) {
        // 校验输入参数是否为空
        if (StrUtil.isEmpty(location)) {
            throw new IllegalArgumentException("OSS URI 不能为空");
        }

        // 尝试创建 URI 对象，捕获格式错误
        try {
            this.uri = URI.create(location);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的 OSS URI 格式: " + location, e);
        }

        // 校验协议是否为 oss
        if (!OSS_PROTOCOL.equals(this.uri.getScheme())) {
            throw new IllegalArgumentException("OSS URI 必须以 oss:// 开头: " + location);
        }

        // 获取并校验 bucket 名称（即 authority 部分）
        String authority = this.uri.getAuthority();
        if (StrUtil.isEmpty(authority)) {
            throw new IllegalArgumentException("OSS URI 中缺少 bucket 名称: " + location);
        }
        this.bucketName = authority;

        // 提取 object key 并处理路径前缀
        String path = this.uri.getPath();
        if (StrUtil.isEmpty(path) || "/".equals(path)) {
            this.objectKey = "";
        } else {
            // 去除路径开头的斜杠
            this.objectKey = path.startsWith("/") ? path.substring(1) : path;
        }

        // 判断当前 URI 是否仅指向 bucket（无具体对象）
        this.isBucket = StrUtil.isEmpty(this.objectKey);

        // 记录解析结果用于调试
        log.debug("解析 OSS URI: bucket={}, objectKey={}, isBucket={}", bucketName, objectKey, isBucket);
    }

    /**
     * 验证是否为有效的 OSS URI。
     *
     * @param location URI 字符串
     * @return true 如果是有效的 OSS URI
     */
    public static boolean isValidOssUri(String location) {
        // 检查输入是否为空或不以 OSS 协议前缀开头
        if (StrUtil.isEmpty(location) || !location.startsWith(OSS_SCHEME_PREFIX)) {
            return false;
        }

        try {
            // 解析 URI 并验证协议和权限部分
            URI uri = URI.create(location);
            return OSS_PROTOCOL.equals(uri.getScheme()) && StrUtil.isNotEmpty(uri.getAuthority());
        } catch (Exception e) {
            log.warn("URI 解析失败: {}", location, e);
            return false;
        }
    }

}
