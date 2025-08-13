package com.gls.athena.common.bean.base;

import java.time.LocalDateTime;

/**
 * 领域对象基础接口
 * 定义了系统中所有领域对象需要实现的基础属性和行为
 * 包含通用的审计字段如创建信息、更新信息、版本控制等
 *
 * @author george
 */
public interface IDomain {
    /**
     * 获取记录的唯一标识
     *
     * @return 主键ID
     */
    Long getId();

    /**
     * 设置记录的唯一标识
     *
     * @param id 主键ID
     */
    void setId(Long id);

    /**
     * 获取租户标识
     * 用于多租户系统的数据隔离
     *
     * @return 租户ID
     */
    Long getTenantId();

    /**
     * 设置租户标识
     *
     * @param tenantId 租户ID
     */
    void setTenantId(Long tenantId);

    /**
     * 获取数据版本号
     * 用于实现乐观锁控制
     *
     * @return 版本号
     */
    Integer getVersion();

    /**
     * 设置数据版本号
     *
     * @param version 版本号
     */
    void setVersion(Integer version);

    /**
     * 获取逻辑删除标记
     *
     * @return true: 已删除; false: 正常
     */
    Boolean getDeleted();

    /**
     * 设置逻辑删除标记
     *
     * @param deleted true: 标记为已删除; false: 标记为正常
     */
    void setDeleted(Boolean deleted);

    /**
     * 获取创建用户ID
     * 记录创建该数据的用户标识
     *
     * @return 创建用户的ID
     */
    Long getCreateUserId();

    /**
     * 设置创建用户ID
     *
     * @param createUserId 创建用户的ID
     */
    void setCreateUserId(Long createUserId);

    /**
     * 获取创建用户名称
     * 记录创建该数据的用户名称，便于显示和查询
     *
     * @return 创建用户的名称
     */
    String getCreateUserName();

    /**
     * 设置创建用户名称
     *
     * @param createUserName 创建用户的名称
     */
    void setCreateUserName(String createUserName);

    /**
     * 获取数据创建时间
     * 记录该条数据首次创建的时间戳
     *
     * @return 数据创建时间
     */
    LocalDateTime getCreateTime();

    /**
     * 设置数据创建时间
     *
     * @param createTime 数据创建时间
     */
    void setCreateTime(LocalDateTime createTime);

    /**
     * 获取最后更新用户ID
     * 记录最后一次修改该数据的用户标识
     *
     * @return 更新用户的ID
     */
    Long getUpdateUserId();

    /**
     * 设置最后更新用户ID
     *
     * @param updateUserId 更新用户的ID
     */
    void setUpdateUserId(Long updateUserId);

    /**
     * 获取最后更新用户名称
     * 记录最后一次修改该数据的用户名称，便于显示和查询
     *
     * @return 更新用户的名称
     */
    String getUpdateUserName();

    /**
     * 设置最后更新用户名称
     *
     * @param updateUserName 更新用户的名称
     */
    void setUpdateUserName(String updateUserName);

    /**
     * 获取最后更新时间
     * 记录该条数据最后一次被修改的时间戳
     *
     * @return 数据最后更新时间
     */
    LocalDateTime getUpdateTime();

    /**
     * 设置最后更新时间
     *
     * @param updateTime 数据最后更新时间
     */
    void setUpdateTime(LocalDateTime updateTime);
}
