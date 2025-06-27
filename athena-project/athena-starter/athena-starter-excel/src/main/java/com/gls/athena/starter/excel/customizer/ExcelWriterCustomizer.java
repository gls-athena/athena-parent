package com.gls.athena.starter.excel.customizer;

import cn.hutool.core.util.StrUtil;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Excel写入构建器自定义器
 * 用于配置和定制化Excel写入过程中的各项参数
 *
 * @author george
 * @since 1.0.0
 */
public class ExcelWriterCustomizer extends BaseWriterCustomizer<ExcelWriterBuilder> {

    /**
     * Excel响应配置
     */
    private final ExcelResponse response;

    /**
     * 构造自定义器实例
     *
     * @param response Excel响应配置注解
     */
    private ExcelWriterCustomizer(ExcelResponse response) {
        super(response.config());
        this.response = response;
    }

    /**
     * 构建ExcelWriter对象用于写入Excel数据
     *
     * @param outputStream  输出流，用于指定Excel文件的写入位置
     * @param excelResponse Excel响应对象，包含Excel写入所需的配置和数据
     * @return ExcelWriter实例，可用于后续的Excel数据写入操作
     */
    public static ExcelWriter build(OutputStream outputStream, ExcelResponse excelResponse) {
        // 创建ExcelWriterBuilder并应用自定义配置
        ExcelWriterBuilder builder = FastExcel.write(outputStream);
        ExcelWriterCustomizer customizer = new ExcelWriterCustomizer(excelResponse);
        customizer.customize(builder);

        // 构建并返回ExcelWriter实例
        return builder.build();
    }

    /**
     * 执行Excel写入构建器的定制化配置
     * <p>
     * 该方法用于对Excel写入构建器进行定制化配置，包括基础配置、密码保护配置、Excel类型配置、CSV相关配置以及模板配置。
     * 该方法首先调用父类的customize方法进行基础配置，然后根据excelResponse中的属性值对ExcelWriterBuilder进行进一步的配置。
     *
     * @param builder Excel写入构建器，用于配置Excel写入的相关参数
     */
    @SneakyThrows
    @Override
    public void configure(ExcelWriterBuilder builder) {
        // 基础配置：设置自动关闭流、内存模式、异常时是否写入Excel
        builder.autoCloseStream(response.autoCloseStream())
                .inMemory(response.inMemory())
                .writeExcelOnException(response.writeExcelOnException());

        // 密码保护配置：如果设置了密码，则对Excel文件进行密码保护
        if (StrUtil.isNotEmpty(response.password())) {
            builder.password(response.password());
        }

        // Excel类型配置：如果指定了Excel类型，则设置Excel文件的类型
        if (response.excelType() != null) {
            builder.excelType(response.excelType());
        }

        // CSV相关配置：如果指定了字符集，则设置CSV文件的字符集，并设置是否包含BOM
        if (StrUtil.isNotEmpty(response.charset())) {
            builder.charset(Charset.forName(response.charset()));
        }
        builder.withBom(response.withBom());

        // 模板配置：如果指定了模板文件路径，则设置Excel文件的模板
        if (StrUtil.isNotEmpty(response.template())) {
            InputStream template = new ClassPathResource(response.template()).getInputStream();
            builder.withTemplate(template);
        }
    }

}
