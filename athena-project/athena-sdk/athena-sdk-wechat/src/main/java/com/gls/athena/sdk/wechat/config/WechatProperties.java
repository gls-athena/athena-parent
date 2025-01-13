package com.gls.athena.sdk.wechat.config;

import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信配置
 *
 * @author george
 */
@Data
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".wechat")
public class WechatProperties {
    /**
     * api host 地址
     */
    private String apiHost = "https://api.weixin.qq.com";
}
