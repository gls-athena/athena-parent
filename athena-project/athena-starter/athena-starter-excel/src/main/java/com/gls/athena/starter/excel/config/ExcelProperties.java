package com.gls.athena.starter.excel.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
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

    /**
     * 异步线程池配置
     */
    private AsyncThreadPool asyncThreadPool = new AsyncThreadPool();

    @Data
    public static class AsyncThreadPool {
        /**
         * 核心线程数
         */
        private Integer corePoolSize = 2;

        /**
         * 最大线程数
         */
        private Integer maxPoolSize = 10;

        /**
         * 队列容量
         */
        private Integer queueCapacity = 100;

        /**
         * 线程空闲时间（秒）
         */
        private Integer keepAliveSeconds = 60;

        /**
         * 线程名前缀
         */
        private String threadNamePrefix = "excel-async-";
    }

}
