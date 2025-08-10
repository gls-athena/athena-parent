package com.gls.athena.common.bean.security;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.security.jackson2.UserDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户信息实体类，用于表示系统中的用户基本信息、安全信息以及关联的角色和组织信息。
 * 实现了 IUser 接口，支持获取用户的角色、权限及组织信息。
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "用户信息", description = "用户信息")
@JsonDeserialize(using = UserDeserializer.class)
public class User extends BaseVo implements IUser<Role, Permission, Organization> {
    /**
     * 用户名：用户登录系统的唯一标识符
     */
    @Schema(title = "用户名", description = "用户名")
    private String username;

    /**
     * 密码：用户的登录凭证，通常为加密存储
     */
    @Schema(title = "密码", description = "密码")
    private String password;

    /**
     * 手机号：用户的联系电话号码
     */
    @Schema(title = "手机号", description = "手机号")
    private String mobile;

    /**
     * 邮箱：用户的电子邮箱地址
     */
    @Schema(title = "邮箱", description = "邮箱")
    private String email;

    /**
     * 姓名：用户的真实姓名
     */
    @Schema(title = "姓名", description = "姓名")
    private String realName;

    /**
     * 昵称：用户在系统中展示的别名
     */
    @Schema(title = "昵称", description = "昵称")
    private String nickName;

    /**
     * 头像：用户头像的URL或文件路径
     */
    @Schema(title = "头像", description = "头像")
    private String avatar;

    /**
     * 语言：用户偏好的语言设置（如 zh-CN、en-US）
     */
    @Schema(title = "语言", description = "语言")
    private String language;

    /**
     * 国家/地区：用户所在的国家或地区代码（如 CN、US）
     */
    @Schema(title = "国家", description = "国家")
    private String locale;

    /**
     * 时区：用户所在时区信息（如 Asia/Shanghai）
     */
    @Schema(title = "时区", description = "时区")
    private String timeZone;

    /**
     * 角色列表：该用户所拥有的角色集合
     */
    @Schema(title = "角色列表", description = "角色列表")
    private List<Role> roles;

    /**
     * 组织列表：该用户所属的组织集合
     */
    @Schema(title = "组织列表", description = "组织列表")
    private List<Organization> organizations;

}
