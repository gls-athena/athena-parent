package com.gls.athena.common.bean.security;

import com.gls.athena.common.bean.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Set;

/**
 * 社交用户
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SocialUser extends BaseVo implements OAuth2User {
    /**
     * 权限
     */
    private Set<GrantedAuthority> authorities;
    /**
     * 用户信息（社交平台）
     */
    private Map<String, Object> attributes;
    /**
     * 用户名 用户唯一标识(社交平台)
     */
    private String name;
    /**
     * 社交平台应用id
     */
    private String registrationId;
    /**
     * 系统用户
     */
    private User user;
    /**
     * 绑定状态 true 已绑定 false 未绑定
     */
    private boolean bindStatus;

}
