package com.gls.athena.common.bean.security;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.security.jackson2.SocialUserDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

/**
 * 社交用户
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "社交用户", description = "社交用户")
@JsonDeserialize(using = SocialUserDeserializer.class)
public class SocialUser extends BaseVo implements OidcUser {
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

    /**
     * 获取用户声明信息
     *
     * @return 用户的属性映射，包含从社交平台获取的所有用户信息
     */
    @Override
    public Map<String, Object> getClaims() {
        return this.attributes;
    }

    /**
     * 获取OIDC用户信息对象
     *
     * @return 基于当前用户属性构建的OidcUserInfo实例
     */
    @Override
    public OidcUserInfo getUserInfo() {
        return new OidcUserInfo(this.attributes);
    }

    /**
     * 获取ID令牌
     *
     * @return 返回null，表示当前实现不支持获取ID令牌
     */
    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    /**
     * 获取用户权限集合
     *
     * @return 包含SOCIAL_USER权限的集合，用于标识该用户为社交登录用户
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return CollUtil.newArrayList(new SimpleGrantedAuthority("SOCIAL_USER"));
    }
}
