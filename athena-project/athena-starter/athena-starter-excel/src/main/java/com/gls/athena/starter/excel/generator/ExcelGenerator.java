/**
 * Excel生成器接口，用于定义生成Excel文件的标准方式
 * 此接口的主要作用是提供一个统一的方法来生成Excel文件，以及一个默认方法来判断当前生成器是否支持处理给定的Excel响应
 */
package com.gls.athena.starter.excel.generator;

import com.gls.athena.starter.excel.annotation.ExcelResponse;

import java.io.OutputStream;

/**
 * Excel生成器接口
 *
 * @author lizy19
 */
public interface ExcelGenerator {

    /**
     * 生成Excel文件的主要方法
     *
     * @param data          要写入Excel文件的数据，可以是任意类型，但需要与具体实现兼容
     * @param excelResponse 用于获取Excel文件生成的配置和元数据的ExcelResponse对象
     * @param outputStream  用于输出生成的Excel文件的流，通常是响应给用户的输出流
     * @throws Exception 如果在生成Excel文件的过程中发生任何错误，则抛出此异常
     */
    void generate(Object data, ExcelResponse excelResponse, OutputStream outputStream) throws Exception;

    /**
     * 判断当前Excel生成器是否支持处理给定的Excel响应的默认方法
     *
     * @param excelResponse 要检查的ExcelResponse对象
     * @return 如果当前生成器支持处理给定的Excel响应，则返回true；否则返回false
     */
    default boolean supports(ExcelResponse excelResponse) {
        return excelResponse.generator().equals(this.getClass());
    }
}
