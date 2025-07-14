package com.gls.athena.starter.data.jpa.support;

import com.gls.athena.common.bean.security.LoginUserHelper;
import com.gls.athena.common.core.constant.IConstants;
import com.gls.athena.starter.data.jpa.base.BaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JPA实体默认监听器
 * <p>
 * 自动填充实体审计字段，包括创建人、更新人、时间戳等信息。
 * 支持多租户场景下的数据隔离。
 *
 * @author george
 * @since 1.0.0
 */
@Slf4j
@Component
public class DefaultEntityListener {

    /**
     * 实体持久化前处理
     * <p>
     * 自动设置创建和更新相关的审计字段，包括：
     * <ul>
     *     <li>租户ID - 多租户数据隔离</li>
     *     <li>删除标志 - 逻辑删除标记</li>
     *     <li>创建人信息 - 用户ID和姓名</li>
     *     <li>创建时间 - 当前时间戳</li>
     *     <li>更新人信息 - 用户ID和姓名</li>
     *     <li>更新时间 - 当前时间戳</li>
     * </ul>
     *
     * @param entity 待保存的实体对象，需继承自{@link BaseEntity}
     */
    @PrePersist
    public void prePersist(BaseEntity entity) {
        log.debug("Pre-persist processing for entity: {}", entity.getClass().getSimpleName());

        // 获取当前用户上下文信息
        Long userId = LoginUserHelper.getCurrentUserId().orElse(IConstants.DEFAULT_USER_ID);
        String userRealName = LoginUserHelper.getCurrentUserRealName().orElse(IConstants.DEFAULT_USER_USERNAME);
        Long tenantId = LoginUserHelper.getCurrentUserTenantId().orElse(IConstants.DEFAULT_TENANT_ID);
        Date now = new Date();

        // 设置审计字段
        entity.setTenantId(tenantId);
        entity.setDeleted(false);
        entity.setCreateUserId(userId);
        entity.setCreateUserName(userRealName);
        entity.setCreateTime(now);
        entity.setUpdateUserId(userId);
        entity.setUpdateUserName(userRealName);
        entity.setUpdateTime(now);
    }

    /**
     * 实体更新前处理
     * <p>
     * 自动更新修改人和修改时间字段，确保每次更新操作都能追踪到操作者信息。
     *
     * @param entity 待更新的实体对象，需继承自{@link BaseEntity}
     */
    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        log.debug("Pre-update processing for entity: {}", entity.getClass().getSimpleName());

        // 获取当前用户信息并更新修改相关字段
        Long userId = LoginUserHelper.getCurrentUserId().orElse(IConstants.DEFAULT_USER_ID);
        String userRealName = LoginUserHelper.getCurrentUserRealName().orElse(IConstants.DEFAULT_USER_USERNAME);

        entity.setUpdateUserId(userId);
        entity.setUpdateUserName(userRealName);
        entity.setUpdateTime(new Date());
    }
}
