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
 * 阿里云OSS资源访问实现
 *
 * <p>实现{@link WritableResource}接口，提供阿里云OSS对象的读写操作能力。
 * 该实现支持对OSS中的对象进行读取和写入操作，同时也支持对存储空间的基本操作。</p>
 *
 * <p>支持的URI格式:
 * <ul>
 *     <li>oss://bucketName/objectKey - 访问具体对象</li>
 *     <li>oss://bucketName - 访问存储空间</li>
 * </ul>
 * </p>
 *
 * <p>使用示例:
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
 * </p>
 *
 * @author george
 * @see WritableResource
 * @see Resource
 */
public class OssResource implements WritableResource {
    /**
     * OSS资源的URI位置
     * 格式: oss://bucketName/objectKey
     */
    private final URI location;

    /**
     * OSS存储空间名称
     * 从URI的authority部分获取
     */
    private final String bucketName;

    /**
     * OSS对象键
     * 从URI的path部分获取，去除开头的'/'
     */
    private final String objectKey;

    /**
     * 构造函数
     *
     * @param location 位置
     */
    public OssResource(String location) {
        this.location = URI.create(location);
        this.bucketName = this.location.getAuthority();
        if (StrUtil.isEmpty(this.location.getPath())) {
            this.objectKey = "";
        } else {
            this.objectKey = this.location.getPath().substring(1);
        }
    }

    /**
     * 获取OSS对象的输出流，用于写入数据
     *
     * <p>该方法使用管道流实现异步上传，避免占用过多内存</p>
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
     * 获取OSS对象的输入流，用于读取数据
     *
     * @return 用于读取数据的输入流
     * @throws FileNotFoundException 当文件不存在时
     * @throws IllegalStateException 当试图对存储空间创建输入流时
     * @throws IOException           当创建输入流过程中发生IO错误时
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(String.format("文件不存在: %s", location));
        }

        if (isBucket()) {
            throw new IllegalStateException(String.format("无法对存储空间创建输入流: %s", location));
        }

        return SpringUtil.getBean(OSS.class)
                .getObject(bucketName, objectKey)
                .getObjectContent();
    }

    /**
     * 判断当前资源是否为存储空间
     */
    private boolean isBucket() {
        return StrUtil.isEmpty(objectKey);
    }

    /**
     * 判断OSS资源是否存在
     *
     * @return 如果是存储空间，返回存储空间是否存在；如果是对象，返回对象是否存在
     */
    @Override
    public boolean exists() {
        OSS oss = SpringUtil.getBean(OSS.class);
        return isBucket() ?
                oss.doesBucketExist(bucketName) :
                oss.doesObjectExist(bucketName, objectKey);
    }

    @Override
    public URL getURL() throws IOException {
        // 返回URL
        return this.location.toURL();
    }

    @Override
    public URI getURI() throws IOException {
        // 返回位置
        return this.location;
    }

    @Override
    public File getFile() throws IOException {
        // 抛出异常
        throw new UnsupportedOperationException(getDescription() + " 无法解析为绝对文件路径");
    }

    /**
     * 获取OSS对象的内容长度
     *
     * @return 对象的字节数，如果是存储空间则返回0
     * @throws IOException 当获取元数据失败时
     */
    @Override
    public long contentLength() throws IOException {
        // 判断是否是存储空间
        if (isBucket()) {
            return 0;
        }
        // 获取对象元数据
        OSS oss = SpringUtil.getBean(OSS.class);
        return oss.getObjectMetadata(bucketName, objectKey).getContentLength();
    }

    @Override
    public long lastModified() throws IOException {
        // 判断是否是存储空间
        if (isBucket()) {
            return 0;
        }
        // 获取对象元数据
        OSS oss = SpringUtil.getBean(OSS.class);
        return oss.getObjectMetadata(bucketName, objectKey).getLastModified().getTime();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        // 创建oss资源
        return new OssResource(relativePath);
    }

    @Override
    public String getFilename() {
        // 返回存储空间名称或对象键
        return isBucket() ? bucketName : objectKey;
    }

    @Override
    public String getDescription() {
        return String.format("OSS资源 [%s]", location);
    }
}
