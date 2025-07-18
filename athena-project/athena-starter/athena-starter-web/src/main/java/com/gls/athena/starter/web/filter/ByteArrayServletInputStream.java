package com.gls.athena.starter.web.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * ByteArrayServletInputStream 类用于将字节数组包装为 ServletInputStream。
 * 该类可以用于在需要 InputStream 的场景中，将字节数组作为输入流进行处理。
 * <p>
 * 主要用途包括但不限于：
 * - 在测试环境中模拟 HTTP 请求的输入流。
 * - 在处理文件上传时，将文件内容作为输入流进行处理。
 *
 * @author george
 */
public class ByteArrayServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream inputStream;

    /**
     * 构造函数，初始化 ByteArrayServletInputStream 实例。
     *
     * @param data 要包装为输入流的字节数组。
     */
    public ByteArrayServletInputStream(byte[] data) {
        this.inputStream = new ByteArrayInputStream(data);
    }

    /**
     * 读取输入流中的下一个字节。
     *
     * @return 下一个字节的数据，如果到达流末尾则返回 -1。
     * @throws IOException 如果发生 I/O 错误。
     */
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    /**
     * 检查输入流是否已准备好被读取。
     *
     * @return 如果输入流中有数据可读，则返回 true；否则返回 false。
     */
    @Override
    public boolean isReady() {
        return inputStream.available() > 0;
    }

    /**
     * 检查输入流是否已完成读取。
     *
     * @return 如果输入流已读取完毕，则返回 true；否则返回 false。
     */
    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }

    /**
     * 设置输入流的读取监听器。
     *
     * @param readListener 读取监听器实例。
     */
    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
