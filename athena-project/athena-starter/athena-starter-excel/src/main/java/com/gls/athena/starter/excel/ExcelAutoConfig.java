package com.gls.athena.starter.excel;

import com.gls.athena.starter.excel.annotation.ExcelResponse;
import com.gls.athena.starter.excel.generator.ExcelGenerator;
import com.gls.athena.starter.file.generator.FileGeneratorManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Excel自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
public class ExcelAutoConfig {
    /**
     * 创建Excel生成器管理器Bean
     * 当容器中不存在名为"excelGeneratorManager"的Bean时，创建一个新的FileGeneratorManager实例
     *
     * @param excelGenerators Excel生成器列表，用于初始化管理器
     * @return Excel文件生成器管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "excelGeneratorManager")
    public FileGeneratorManager<ExcelResponse> excelGeneratorManager(List<ExcelGenerator> excelGenerators) {
        return new FileGeneratorManager<>(excelGenerators);
    }
}
