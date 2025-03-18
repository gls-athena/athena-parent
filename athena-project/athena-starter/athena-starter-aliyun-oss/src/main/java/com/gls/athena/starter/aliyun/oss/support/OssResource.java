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
     * <p>实现原理：通过PipedOutputStream和PipedInputStream建立管道连接，将数据写入流操作与OSS上传操作解耦。
     * 主线程返回输出流供业务写入，后台线程异步从输入流读取数据并上传至OSS。</p>
     *
     * @return 用于写入数据的输出流，调用方关闭输出流即表示数据写入完成
     * @throws FileNotFoundException 当目标文件不存在时
     * @throws IOException           当创建输出流或上传过程中发生IO错误时
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(String.format("目标文件不存在: %s", location));
        }

        // 创建管道流对实现异步IO：outputStream用于业务写入，inputStream用于后台读取上传
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);

        // 使用独立线程池提交OSS上传任务，防止阻塞主线程
        ExecutorService ossTaskExecutor = SpringUtil.getBean(ExecutorService.class);
        ossTaskExecutor.submit(() -> {
            // try-with-resource确保输入流正确关闭，自动回收管道资源
            try (InputStream is = inputStream) {
                // 通过OSS SDK将输入流内容上传到指定存储桶和对象
                SpringUtil.getBean(OSS.class).putObject(bucketName, objectKey, is);
            } catch (IOException e) {
                throw new RuntimeException("OSS文件上传失败", e);
            }
        });

        return outputStream;
    }

    /**
     * 获取OSS对象的输入流，用于读取指定路径的文件内容
     * <p>
     * 实现步骤：
     * 1. 前置校验：检查文件是否存在及路径类型
     * 2. 通过OSS客户端获取文件数据流
     *
     * @return InputStream 用于读取文件数据的输入流，调用方需负责关闭该流
     * @throws FileNotFoundException 当指定路径不存在有效文件时抛出（错误信息包含文件路径）
     * @throws IllegalStateException 当尝试对存储空间（bucket）创建输入流时抛出（错误信息包含非法路径）
     * @throws IOException           当OSS客户端操作过程中发生网络异常或权限错误时抛出
     */
    @Override
    public InputStream getInputStream() throws IOException {
        // 前置校验：检查目标文件是否存在
        if (!exists()) {
            throw new FileNotFoundException(String.format("文件不存在: %s", location));
        }

        // 防御性检查：阻止对存储空间层级的非法操作
        if (isBucket()) {
            throw new IllegalStateException(String.format("无法对存储空间创建输入流: %s", location));
        }

        // 通过Spring上下文获取OSS客户端实例，并创建目标文件的数据流
        return SpringUtil.getBean(OSS.class)
                .getObject(bucketName, objectKey)
                .getObjectContent();
    }

    /**
     * 判断当前资源是否为存储空间（Bucket）类型
     * <p>
     * 通过检查对象键（objectKey）是否为空来判定资源类型：
     * 当对象键为空时表示操作目标是存储空间本身，否则表示操作目标是存储空间中的具体对象
     *
     * @return boolean 判断结果
     * true - 当前资源为存储空间（Bucket）
     * false - 当前资源为存储空间中的对象（Object）
     */
    private boolean isBucket() {
        return StrUtil.isEmpty(objectKey);
    }

    /**
     * 判断当前OSS资源（存储空间或对象）是否存在
     *
     * <p>通过Spring上下文获取OSS客户端实例，根据资源类型执行存在性检查：
     * 1. 当资源为存储空间时，调用oss.doesBucketExist检查存储空间是否存在
     * 2. 当资源为对象时，调用oss.doesObjectExist检查对象是否存在
     *
     * @return boolean 类型检查结果，true表示存在，false表示不存在。具体语义：
     * 若当前资源是存储空间，返回存储空间存在状态；
     * 若当前资源是对象，返回对象存在状态
     */
    @Override
    public boolean exists() {
        // 从Spring容器获取OSS客户端实例
        OSS oss = SpringUtil.getBean(OSS.class);

        // 根据资源类型选择对应的存在性检查方式
        return isBucket() ?
                oss.doesBucketExist(bucketName) :
                oss.doesObjectExist(bucketName, objectKey);
    }

    /**
     * 获取当前资源对应的URL对象
     * <p>
     * 通过将内部维护的location属性转换为标准URL格式实现。
     *
     * @return 表示当前资源位置的URL对象
     * @throws IOException 当URL转换过程中发生I/O异常时抛出
     */
    @Override
    public URL getURL() throws IOException {
        // 返回URL
        return this.location.toURL();
    }

    /**
     * 获取当前资源的URI。
     * <p>
     * 该方法返回当前资源的位置（URI），通常用于标识资源的唯一路径或地址。
     *
     * @return 返回当前资源的URI，类型为 {@link URI}。
     * @throws IOException 如果在获取URI的过程中发生I/O错误，则抛出该异常。
     */
    @Override
    public URI getURI() throws IOException {
        return this.location;
    }

    /**
     * 获取文件对象。
     * <p>
     * 该方法被重写以抛出一个 {@code UnsupportedOperationException} 异常，表示当前对象无法解析为绝对文件路径。
     *
     * @return 该方法不会返回任何文件对象，而是直接抛出异常。
     * @throws IOException 如果无法获取文件对象，抛出 {@code UnsupportedOperationException} 异常。
     */
    @Override
    public File getFile() throws IOException {
        // 抛出异常，表示当前对象无法解析为绝对文件路径
        throw new UnsupportedOperationException(getDescription() + " 无法解析为绝对文件路径");
    }

    /**
     * 获取OSS对象的内容长度
     * <p>
     * 该方法用于获取指定OSS对象的内容长度（字节数）。如果当前对象是存储空间（Bucket），则返回0。
     * 该方法通过调用OSS服务的API获取对象的元数据，并从中提取内容长度。
     *
     * @return 对象的字节数，如果是存储空间则返回0
     * @throws IOException 当获取元数据失败时抛出此异常
     */
    @Override
    public long contentLength() throws IOException {
        // 判断当前对象是否为存储空间，如果是则直接返回0
        if (isBucket()) {
            return 0;
        }

        // 通过SpringUtil获取OSS实例，并调用其API获取对象的元数据，最后返回内容长度
        OSS oss = SpringUtil.getBean(OSS.class);
        return oss.getObjectMetadata(bucketName, objectKey).getContentLength();
    }

    /**
     * 获取对象的最后修改时间。
     * <p>
     * 该方法首先判断当前对象是否为存储空间（Bucket），如果是存储空间，则返回0表示没有最后修改时间。
     * 如果不是存储空间，则通过OSS客户端获取对象的元数据，并返回对象的最后修改时间。
     *
     * @return 返回对象的最后修改时间，以毫秒为单位。如果对象是存储空间，则返回0。
     * @throws IOException 如果获取对象元数据时发生IO异常，则抛出该异常。
     */
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

    /**
     * 根据给定的相对路径创建一个新的 OSS 资源。
     * <p>
     * 该方法重写了父类的 `createRelative` 方法，用于在 OSS 存储中创建一个与当前资源相对的新资源。
     *
     * @param relativePath 相对于当前资源的路径，用于指定新资源的位置。
     * @return 返回一个表示新资源的 {@link OssResource} 对象。
     * @throws IOException 如果在创建资源过程中发生 I/O 错误，则抛出此异常。
     */
    @Override
    public Resource createRelative(String relativePath) throws IOException {
        // 创建oss资源
        return new OssResource(relativePath);
    }

    /**
     * 获取存储空间名称或对象键。
     * 该方法根据当前对象是否为存储空间（Bucket）来决定返回存储空间名称还是对象键。
     *
     * @return 如果当前对象是存储空间，则返回存储空间名称；否则返回对象键。
     */
    @Override
    public String getFilename() {
        // 根据是否为存储空间返回相应的名称或键
        return isBucket() ? bucketName : objectKey;
    }

    /**
     * 获取OSS资源的描述信息。
     * 该函数返回一个格式化后的字符串，描述OSS资源的位置信息。
     *
     * @return 返回一个字符串，格式为"OSS资源 [location]"，其中location是资源的具体位置。
     */
    @Override
    public String getDescription() {
        return String.format("OSS资源 [%s]", location);
    }
}
