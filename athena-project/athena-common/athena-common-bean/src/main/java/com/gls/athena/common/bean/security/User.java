package com.gls.athena.common.bean.security;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.security.jackson2.UserDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户信息
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "用户信息", description = "用户信息")
@JsonDeserialize(using = UserDeserializer.class)
public class User extends BaseVo implements IUser<Role, Permission, Organization> {
    /**
     * 用户名
     */
    @Schema(title = "用户名", description = "用户名")
    private String username;
    /**
     * 密码
     */
    @Schema(title = "密码", description = "密码")
    private String password;
    /**
     * 手机号
     */
    @Schema(title = "手机号", description = "手机号")
    private String mobile;
    /**
     * 邮箱
     */
    @Schema(title = "邮箱", description = "邮箱")
    private String email;
    /**
     * 姓名
     */
    @Schema(title = "姓名", description = "姓名")
    private String realName;
    /**
     * 昵称
     */
    @Schema(title = "昵称", description = "昵称")
    private String nickName;
    /**
     * 头像
     */
    @Schema(title = "头像", description = "头像")
    private String avatar;
    /**
     * 性别
     */
    @Schema(title = "语言", description = "语言")
    private String language;
    /**
     * 国家
     */
    @Schema(title = "国家", description = "国家")
    private String locale;
    /**
     * 时区
     */
    @Schema(title = "时区", description = "时区")
    private String timeZone;
    /**
     * 角色列表
     */
    @Schema(title = "角色列表", description = "角色列表")
    private List<Role> roles;
    /**
     * 组织列表
     */
    @Schema(title = "组织列表", description = "组织列表")
    private List<Organization> organizations;

}
