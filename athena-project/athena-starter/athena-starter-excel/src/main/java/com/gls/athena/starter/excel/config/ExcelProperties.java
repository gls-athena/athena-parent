package com.gls.athena.starter.excel.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Excel配置属性类，用于读取和管理Excel相关的配置信息。
 * 该类通过@ConfigurationProperties注解自动绑定application.yml中前缀为
 * IConstants.BASE_PROPERTIES_PREFIX + ".excel"的配置项。
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".excel")
public class ExcelProperties extends BaseProperties {
    /**
     * 模板路径
     */
    private String templatePath = "classpath:/templates/excel";

    /**
     * 异步导出文件存储目录
     */
    private String asyncExportDir = System.getProperty("java.io.tmpdir") + "/excel-exports";

    /**
     * 异步导出任务超时时间（分钟）
     */
    private Integer asyncTimeoutMinutes = 30;

    /**
     * 异步导出任务清理间隔（分钟）
     */
    private Integer taskCleanupIntervalMinutes = 60;

    /**
     * 异步导出文件保留天数
     */
    private Integer fileRetentionDays = 7;

}
