package com.gls.athena.starter.aliyun.oss.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.oss.OSS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * 阿里云OSS资源访问实现类，提供对OSS对象的读写操作。
 *
 * <p>支持的URI格式：
 * <ul>
 *     <li>oss://bucketName/objectKey - 访问具体对象</li>
 *     <li>oss://bucketName - 访问存储空间</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * // 创建OSS资源
 * OssResource resource = new OssResource("oss://my-bucket/path/to/file.txt");
 *
 * // 写入内容
 * try (OutputStream out = resource.getOutputStream()) {
 *     out.write("Hello World".getBytes());
 * }
 *
 * // 读取内容
 * try (InputStream in = resource.getInputStream()) {
 *     // 处理输入流
 * }
 * </pre>
 *
 * @author george
 * @see WritableResource
 * @see Resource
 */
public class OssResource implements WritableResource {
    /**
     * OSS资源的URI位置，格式为：oss://bucketName/objectKey
     */
    private final URI location;

    /**
     * OSS存储空间名称，从URI的authority部分获取
     */
    private final String bucketName;

    /**
     * OSS对象键，从URI的path部分获取，去除开头的'/'
     */
    private final String objectKey;

    /**
     * 构造函数，初始化OSS资源。
     *
     * @param location 资源位置，格式为oss://bucketName/objectKey
     */
    public OssResource(String location) {
        this.location = URI.create(location);
        this.bucketName = this.location.getAuthority();
        this.objectKey = StrUtil.isEmpty(this.location.getPath()) ? "" : this.location.getPath().substring(1);
    }

    /**
     * 获取OSS对象的输出流，用于写入数据。
     *
     * <p>使用管道流实现异步上传，避免内存占用过高。</p>
     *
     * @return 用于写入数据的输出流
     * @throws FileNotFoundException 当目标文件不存在时
     * @throws IOException           当创建输出流或上传过程中发生IO错误时
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(String.format("目标文件不存在: %s", location));
        }

        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);

        ExecutorService ossTaskExecutor = SpringUtil.getBean(ExecutorService.class);
        ossTaskExecutor.submit(() -> {
            try (InputStream is = inputStream) {
                SpringUtil.getBean(OSS.class).putObject(bucketName, objectKey, is);
            } catch (IOException e) {
                throw new RuntimeException("OSS文件上传失败", e);
            }
        });

        return outputStream;
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
            throw new FileNotFoundException(String.format("文件不存在: %s", location));
        }
        if (isBucket()) {
            throw new IllegalStateException(String.format("无法对存储空间创建输入流: %s", location));
        }
        return SpringUtil.getBean(OSS.class).getObject(bucketName, objectKey).getObjectContent();
    }

    /**
     * 判断当前资源是否为存储空间（Bucket）。
     *
     * @return true - 当前资源为存储空间；false - 当前资源为对象
     */
    private boolean isBucket() {
        return StrUtil.isEmpty(objectKey);
    }

    /**
     * 判断当前OSS资源（存储空间或对象）是否存在。
     *
     * @return true - 资源存在；false - 资源不存在
     */
    @Override
    public boolean exists() {
        OSS oss = SpringUtil.getBean(OSS.class);
        return isBucket() ? oss.doesBucketExist(bucketName) : oss.doesObjectExist(bucketName, objectKey);
    }

    /**
     * 获取当前资源对应的URL对象。
     *
     * @return 表示当前资源位置的URL对象
     * @throws IOException 当URL转换过程中发生IO错误时
     */
    @Override
    public URL getURL() throws IOException {
        return this.location.toURL();
    }

    /**
     * 获取当前资源的URI。
     *
     * @return 当前资源的URI
     * @throws IOException 当获取URI过程中发生IO错误时
     */
    @Override
    public URI getURI() throws IOException {
        return this.location;
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
        if (isBucket()) {
            return 0;
        }
        return SpringUtil.getBean(OSS.class).getObjectMetadata(bucketName, objectKey).getContentLength();
    }

    /**
     * 获取对象的最后修改时间。
     *
     * @return 对象的最后修改时间（毫秒），如果是存储空间则返回0
     * @throws IOException 当获取元数据失败时
     */
    @Override
    public long lastModified() throws IOException {
        if (isBucket()) {
            return 0;
        }
        return SpringUtil.getBean(OSS.class).getObjectMetadata(bucketName, objectKey).getLastModified().getTime();
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
        return new OssResource(relativePath);
    }

    /**
     * 获取存储空间名称或对象键。
     *
     * @return 如果当前对象是存储空间，则返回存储空间名称；否则返回对象键
     */
    @Override
    public String getFilename() {
        return isBucket() ? bucketName : objectKey;
    }

    /**
     * 获取OSS资源的描述信息。
     *
     * @return 格式为"OSS资源 [location]"的字符串，其中location是资源的具体位置
     */
    @Override
    public String getDescription() {
        return String.format("OSS资源 [%s]", location);
    }
}
