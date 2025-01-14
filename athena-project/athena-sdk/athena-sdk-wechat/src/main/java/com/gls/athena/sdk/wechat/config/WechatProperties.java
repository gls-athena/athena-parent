package com.gls.athena.sdk.wechat.config;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".wechat")
public class WechatProperties extends BaseProperties {
    /**
     * 微信appId
     */
    private String appId;
    /**
     * 微信appSecret
     */
    private String appSecret;
    /**
     * 微信 api host
     */
    private String apiHost = "https://api.weixin.qq.com";
}
