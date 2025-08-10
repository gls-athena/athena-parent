package com.gls.athena.starter.aliyun.oss.support;

import com.gls.athena.starter.aliyun.oss.service.OssClientService;
import com.gls.athena.starter.aliyun.oss.service.OssMetadataService;
import com.gls.athena.starter.aliyun.oss.service.OssStreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * OSS 资源包装类，实现了 Spring 的 WritableResource 接口。
 * <p>
 * 用于通过 OSS URI 进行资源的读取与写入操作，支持流式处理和元数据管理。
 * <pre>
 * // 读取内容示例：
 * try (InputStream in = resource.getInputStream()) {
 *     // 处理输入流
 * }
 * </pre>
 *
 * @author george
 * @see WritableResource
 * @see Resource
 */
@Slf4j
@RequiredArgsConstructor
public class OssResource implements WritableResource {

    /**
     * OSS URI 解析器，负责解析和验证 URI
     */
    private final OssUriParser uriParser;

    /**
     * OSS 客户端服务，负责基本的 OSS 操作
     */
    private final OssClientService ossClientService;

    /**
     * OSS 流服务，负责输入输出流操作
     */
    private final OssStreamService ossStreamService;

    /**
     * OSS 元数据服务，负责元数据操作
     */
    private final OssMetadataService ossMetadataService;

    /**
     * 缓存是否是 bucket 类型资源
     */
    private final boolean isBucket = uriParser.isBucket();

    /**
     * 获取OSS对象的输出流，用于写入数据。
     *
     * @return 用于写入数据的输出流
     * @throws FileNotFoundException 当目标文件不存在时
     * @throws IOException           当创建输出流或上传过程中发生IO错误时
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(String.format("文件不存在: %s", uriParser.getUri()));
        }

        if (isBucket) {
            throw new IllegalStateException(String.format("无法对存储空间创建输出流: %s", uriParser.getUri()));
        }

        return ossStreamService.getOutputStream(uriParser.getBucketName(), uriParser.getObjectKey());
    }

    /**
     * 获取OSS对象的输入流，用于读取文件内容。
     *
     * @return 用于读取文件数据的输入流
     * @throws FileNotFoundException 当指定路径不存在有效文件时
     * @throws IllegalStateException 当尝试对存储空间创建输入流时
     * @throws IOException           当OSS客户端操作过程中发生IO错误时
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(String.format("文件不存在: %s", uriParser.getUri()));
        }
        if (isBucket) {
            throw new IllegalStateException(String.format("无法对存储空间创建输入流: %s", uriParser.getUri()));
        }

        return ossStreamService.getInputStream(uriParser.getBucketName(), uriParser.getObjectKey());
    }

    /**
     * 判断当前OSS资源（存储空间或对象）是否存在。
     *
     * @return true - 资源存在；false - 资源不存在
     */
    @Override
    public boolean exists() {
        return isBucket
                ? ossClientService.doesBucketExist(uriParser.getBucketName())
                : ossClientService.doesObjectExist(uriParser.getBucketName(), uriParser.getObjectKey());
    }

    /**
     * 获取当前资源对应的URL对象。
     *
     * @return 表示当前资源位置的URL对象
     * @throws IOException 当URL转换过程中发生IO错误时
     */
    @Override
    public URL getURL() throws IOException {
        return uriParser.getUri().toURL();
    }

    /**
     * 获取当前资源的URI。
     *
     * @return 当前资源的URI
     * @throws IOException 当获取URI过程中发生IO错误时
     */
    @Override
    public URI getURI() throws IOException {
        return uriParser.getUri();
    }

    /**
     * 获取文件对象。
     *
     * @return 该方法不会返回任何文件对象，而是直接抛出异常
     * @throws IOException 当无法获取文件对象时
     */
    @Override
    public File getFile() throws IOException {
        throw new UnsupportedOperationException(getDescription() + " 无法解析为绝对文件路径");
    }

    /**
     * 获取OSS对象的内容长度。
     *
     * @return 对象的字节数，如果是存储空间则返回0
     * @throws IOException 当获取元数据失败时
     */
    @Override
    public long contentLength() throws IOException {
        if (isBucket) {
            return 0;
        }
        return ossMetadataService.getContentLength(uriParser.getBucketName(), uriParser.getObjectKey());
    }

    /**
     * 获取对象的最后修改时间。
     *
     * @return 对象的最后修改时间（毫秒），如果是存储空间则返回0
     * @throws IOException 当获取元数据失败时
     */
    @Override
    public long lastModified() throws IOException {
        if (isBucket) {
            return 0;
        }
        return ossMetadataService.getLastModified(uriParser.getBucketName(), uriParser.getObjectKey());
    }

    /**
     * 根据相对路径创建一个新的OSS资源。
     *
     * @param relativePath 相对于当前资源的路径
     * @return 表示新资源的OssResource对象
     * @throws IOException 当创建资源过程中发生IO错误时
     */
    @Override
    public Resource createRelative(String relativePath) throws IOException {
        if (relativePath == null) {
            throw new IllegalArgumentException("relativePath 不能为空");
        }

        // 构建新的完整路径
        String newLocation = buildRelativePath(relativePath);
        OssUriParser newUriParser = new OssUriParser(newLocation);
        return new OssResource(newUriParser, ossClientService, ossStreamService, ossMetadataService);
    }

    /**
     * 获取存储空间名称或对象键。
     *
     * @return 如果当前对象是存储空间，则返回存储空间名称；否则返回对象键
     */
    @Override
    public String getFilename() {
        return isBucket ? uriParser.getBucketName() : extractFilename(uriParser.getObjectKey());
    }

    /**
     * 获取OSS资源的描述信息。
     *
     * @return 格式为"OSS资源 [location]"的字符串，其中location是资源的具体位置
     */
    @Override
    public String getDescription() {
        return String.format("OSS资源 [%s]", uriParser.getUri());
    }

    /**
     * 构建相对路径的完整 OSS URI。
     *
     * @param relativePath 相对路径
     * @return 完整的 OSS URI
     */
    private String buildRelativePath(String relativePath) {
        StringBuilder sb = new StringBuilder("oss://").append(uriParser.getBucketName()).append("/");
        if (isBucket) {
            sb.append(relativePath);
        } else {
            String parentPath = getParentPath(uriParser.getObjectKey());
            if (!parentPath.isEmpty()) {
                sb.append(parentPath).append("/");
            }
            sb.append(relativePath);
        }
        return sb.toString();
    }

    /**
     * 获取对象键的父路径。
     */
    private String getParentPath(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return "";
        }
        int lastSlashIndex = objectKey.lastIndexOf('/');
        return lastSlashIndex > 0 ? objectKey.substring(0, lastSlashIndex) : "";
    }

    /**
     * 从对象键中提取文件名。
     */
    private String extractFilename(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return "";
        }
        int lastSlashIndex = objectKey.lastIndexOf('/');
        return lastSlashIndex >= 0 ? objectKey.substring(lastSlashIndex + 1) : objectKey;
    }
}
