package com.gls.athena.security.servlet.client.wechat.domain;

import lombok.Data;

/**
 * 微信用户信息响应
 *
 * @author george
 */
@Data
public class WechatUserInfoResponse {
    /**
     * 用户标识
     */
    private String openid;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 国家
     */
    private String country;
    /**
     * 头像
     */
    private String headImgUrl;
    /**
     * 用户特权信息
     */
    private String privilege;
    /**
     * 用户统一标识
     */
    private String unionid;

}
