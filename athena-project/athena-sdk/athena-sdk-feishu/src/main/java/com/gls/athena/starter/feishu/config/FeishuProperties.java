package com.gls.athena.starter.feishu.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import com.lark.oapi.core.enums.AppType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * 飞书配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".feishu")
public class FeishuProperties extends BaseProperties {
    /**
     * 应用appid
     */
    private String appId;
    /**
     * 应用密钥
     */
    private String appSecret;
    /**
     * 应用类型: 企业应用、应用商店应用
     */
    private AppType appType = AppType.SELF_BUILT;
    /**
     * 调试标识
     */
    private boolean debugFlag = false;
    /**
     * token缓存标识
     */
    private boolean tokenCacheFlag = true;
    /**
     * 服务台id
     */
    private String helpDeskId;
    /**
     * 服务台密钥
     */
    private String helpDeskSecret;
    /**
     * 请求超时时间
     */
    private long requestTimeout = 10;
    /**
     * 超时时间单位
     */
    private TimeUnit requestTimeoutUnit = TimeUnit.SECONDS;

}
