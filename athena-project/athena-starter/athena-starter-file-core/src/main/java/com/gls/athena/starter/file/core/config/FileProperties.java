package com.gls.athena.starter.file.core.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件配置属性类
 * 用于配置文件存储相关参数，包括存储类型、路径和URL前缀等信息
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".file")
public class FileProperties extends BaseProperties {

    /**
     * 文件信息管理器类型
     * 默认为memory(内存存储)
     */
    private String info = "memory";

    /**
     * 文件存储类型
     * 默认为local(本地存储)
     */
    private String storage = "local";

    /**
     * 文件存储路径
     * 默认为upload目录
     */
    private String path = "upload";

    /**
     * 文件访问URL前缀
     * 默认为/files/
     */
    private String urlPrefix = "/files/";

    /**
     * 文件清理配置
     */
    private Cleanup cleanup = new Cleanup();

    /**
     * 文件清理配置类
     */
    @Data
    public static class Cleanup {
        /**
         * 是否启用文件清理任务
         */
        private boolean enabled = false;

        /**
         * 清理任务执行的cron表达式
         * 默认每天凌晨2点执行
         */
        private String cron = "0 0 2 * * ?";

        /**
         * 文件保留天数
         * 默认7天
         */
        private int retentionDays = 7;
    }
}
