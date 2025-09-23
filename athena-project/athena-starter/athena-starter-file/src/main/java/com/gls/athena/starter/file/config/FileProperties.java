package com.gls.athena.starter.file.config;

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
     * 文件存储类型
     * 默认为local(本地存储)
     */
    private String type = "local";

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
}
