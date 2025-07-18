package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.ObjUtil;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteWorkbook;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * WriteWorkbook自定义配置类
 * 用于根据ExcelResponse注解的配置来定制WriteWorkbook实例
 *
 * @author lizy19
 */
public class WriteWorkbookCustomizer extends BaseWriteCustomizer<WriteWorkbook> {

    /**
     * Excel响应配置
     */
    private final ExcelResponse excelResponse;
    /**
     * 输出流
     */
    private final OutputStream outputStream;

    /**
     * 私有构造方法，使用ExcelResponse和输出流初始化WriteWorkbookCustomizer实例
     *
     * @param excelResponse Excel响应配置
     * @param outputStream  输出流
     */
    private WriteWorkbookCustomizer(ExcelResponse excelResponse, OutputStream outputStream) {
        super(excelResponse.config());
        this.excelResponse = excelResponse;
        this.outputStream = outputStream;
    }

    /**
     * 获取ExcelWriter实例
     *
     * @param excelResponse Excel响应配置
     * @param outputStream  输出流
     * @return ExcelWriter实例
     */
    public static ExcelWriter getExcelWriter(ExcelResponse excelResponse, OutputStream outputStream) {
        WriteWorkbook writeWorkbook = getWriteWorkbook(excelResponse, outputStream);
        return new ExcelWriter(writeWorkbook);
    }

    /**
     * 获取配置好的WriteWorkbook实例
     *
     * @param excelResponse Excel响应配置
     * @param outputStream  输出流
     * @return 配置好的WriteWorkbook实例
     */
    public static WriteWorkbook getWriteWorkbook(ExcelResponse excelResponse, OutputStream outputStream) {
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        WriteWorkbookCustomizer writeWorkbookCustomizer = new WriteWorkbookCustomizer(excelResponse, outputStream);
        writeWorkbookCustomizer.customize(writeWorkbook);
        return writeWorkbook;
    }

    /**
     * 自定义WriteWorkbook的写入配置
     *
     * @param writeWorkbook 待配置的WriteWorkbook实例
     */
    @SneakyThrows
    @Override
    protected void customizeWrite(WriteWorkbook writeWorkbook) {
        // 设置Excel文件类型（XLSX、XLS等）
        if (ObjUtil.isNotEmpty(excelResponse.excelType())) {
            writeWorkbook.setExcelType(excelResponse.excelType());
        }

        // 设置输出流
        if (ObjUtil.isNotNull(outputStream)) {
            writeWorkbook.setOutputStream(outputStream);
        }

        // 设置字符编码
        if (ObjUtil.isNotEmpty(excelResponse.charset())) {
            Charset charset = Charset.forName(excelResponse.charset());
            writeWorkbook.setCharset(charset);
        }

        // 设置是否包含BOM（字节顺序标记），主要用于UTF-8编码
        if (ObjUtil.isNotNull(excelResponse.withBom())) {
            writeWorkbook.setWithBom(excelResponse.withBom());
        }

        // 设置Excel模板文件路径（支持从classpath加载）
        if (ObjUtil.isNotEmpty(excelResponse.template())) {
            Resource templateResource = new ClassPathResource(excelResponse.template());
            writeWorkbook.setTemplateInputStream(templateResource.getInputStream());
        }

        // 设置是否自动关闭流
        if (ObjUtil.isNotNull(excelResponse.autoCloseStream())) {
            writeWorkbook.setAutoCloseStream(excelResponse.autoCloseStream());
        }

        // 设置是否强制使用输入流模式
        if (ObjUtil.isNotNull(excelResponse.mandatoryUseInputStream())) {
            writeWorkbook.setMandatoryUseInputStream(excelResponse.mandatoryUseInputStream());
        }

        // 设置Excel文件密码保护
        if (ObjUtil.isNotEmpty(excelResponse.password())) {
            writeWorkbook.setPassword(excelResponse.password());
        }

        // 设置是否使用内存模式（true为内存模式，false为磁盘模式）
        if (ObjUtil.isNotNull(excelResponse.inMemory())) {
            writeWorkbook.setInMemory(excelResponse.inMemory());
        }

        // 设置异常时是否写入Excel文件
        if (ObjUtil.isNotNull(excelResponse.writeExcelOnException())) {
            writeWorkbook.setWriteExcelOnException(excelResponse.writeExcelOnException());
        }
    }
}
