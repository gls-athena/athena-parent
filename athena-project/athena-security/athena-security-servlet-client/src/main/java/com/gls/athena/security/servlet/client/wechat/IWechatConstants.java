package com.gls.athena.security.servlet.client.wechat;

/**
 * 微信相关常量定义接口
 * 包含微信开放平台、公众平台、小程序以及企业微信等平台的标识常量
 *
 * @author george
 * @since 1.0.0
 */
public interface IWechatConstants {
    /**
     * 微信开放平台标识
     * 用于标识使用微信开放平台进行授权的场景
     */
    String WECHAT_OPEN_PROVIDER_ID = "wechat_open";

    /**
     * 微信公众平台标识
     * 用于标识使用微信公众号进行授权的场景
     */
    String WECHAT_MP_PROVIDER_ID = "wechat_mp";

    /**
     * 微信小程序标识
     * 用于标识使用微信小程序进行授权的场景
     */
    String WECHAT_MINI_PROVIDER_ID = "wechat_mini";

    /**
     * 企业微信标识
     * 用于标识使用企业微信进行授权的场景
     */
    String WECHAT_WORK_PROVIDER_ID = "wechat_work";

    /**
     * 企业微信用户登录URI名称
     * 用于获取企业微信用户登录地址的配置键名
     */
    String WECHAT_WORK_USER_LOGIN_URI_NAME = "workUserLoginUri";
}
