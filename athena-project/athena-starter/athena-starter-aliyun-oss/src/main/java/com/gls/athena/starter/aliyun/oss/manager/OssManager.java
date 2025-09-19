package com.gls.athena.starter.aliyun.oss.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * OssManager接口定义了阿里云对象存储服务的基本操作
 * 提供了对OSS存储桶和对象的常用操作方法
 *
 * @author george
 */
public interface OssManager {

    /**
     * 获取指定存储桶和文件路径的输出流
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return OutputStream 输出流对象
     * @throws IOException 如果无法创建输出流则抛出异常
     */
    OutputStream getOutputStream(String bucketName, String filePath) throws IOException;

    /**
     * 获取指定存储桶和文件路径的输入流
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return InputStream 输入流对象
     */
    InputStream getInputStream(String bucketName, String filePath);

    /**
     * 检查指定的存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return boolean 存在返回true，否则返回false
     */
    boolean doesBucketExist(String bucketName);

    /**
     * 检查指定存储桶中的对象是否存在
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return boolean 对象存在返回true，否则返回false
     */
    boolean doesObjectExist(String bucketName, String filePath);

    /**
     * 获取指定对象的内容长度
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return long 对象内容的字节长度
     */
    long getContentLength(String bucketName, String filePath);

    /**
     * 获取指定对象的最后修改时间
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return long 对象最后修改时间的时间戳
     */
    long getLastModified(String bucketName, String filePath);

    /**
     * 将输入流中的数据上传到指定的存储桶和文件路径
     *
     * @param bucketName  存储桶名称
     * @param filePath    文件路径
     * @param inputStream 输入流对象
     */
    void putObject(String bucketName, String filePath, InputStream inputStream);

    /**
     * 生成指定对象的预签名URL
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @param expiration 过期时间
     * @return String 预签名URL字符串
     */
    String generatePresignedUrl(String bucketName, String filePath, Date expiration);

    /**
     * 删除指定存储桶中的对象
     *
     * @param bucketName 存储桶名称
     * @param filePath   文件路径
     * @return boolean 删除成功返回true，否则返回false
     */
    boolean deleteObject(String bucketName, String filePath);
}


