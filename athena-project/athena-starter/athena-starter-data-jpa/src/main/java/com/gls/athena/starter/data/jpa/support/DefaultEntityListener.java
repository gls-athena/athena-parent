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
 * JPA实体操作前的默认监听器
 * 用于自动填充实体的审计字段
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultEntityListener {

    /**
     * 实体保存前的处理
     * 在实体对象持久化到数据库之前，自动填充审计字段，包括创建人、创建时间、更新人、更新时间等。
     * 该方法通常由JPA的@PrePersist注解触发，确保在实体保存前执行。
     *
     * @param entity 待保存的实体对象，必须继承自BaseEntity类
     */
    @PrePersist
    public void prePersist(BaseEntity entity) {
        // 记录日志，输出当前处理的实体类名
        log.debug("Entity pre persist processing: [{}]", entity.getClass().getSimpleName());

        // 获取当前用户上下文信息，包括用户ID、用户真实姓名、租户ID等
        Long userId = LoginUserHelper.getCurrentUserId().orElse(IConstants.DEFAULT_USER_ID);
        String userRealName = LoginUserHelper.getCurrentUserRealName().orElse(IConstants.DEFAULT_USER_USERNAME);
        Long tenantId = LoginUserHelper.getCurrentUserTenantId().orElse(IConstants.DEFAULT_TENANT_ID);
        Date now = new Date();

        // 设置实体的审计字段，包括租户ID、删除标志、创建人、创建时间、更新人、更新时间等
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
     * 实体更新前的处理函数，通常用于在实体更新之前自动执行。
     * 该函数会更新实体的修改人和修改时间字段，确保在每次更新时记录最新的操作信息。
     *
     * @param entity 待更新的实体对象，必须继承自BaseEntity类。
     */
    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        // 记录实体更新前的日志信息，便于调试和追踪
        log.debug("Entity pre update processing: [{}]", entity.getClass().getSimpleName());

        // 获取当前用户信息并更新审计字段
        Long userId = LoginUserHelper.getCurrentUserId().orElse(IConstants.DEFAULT_USER_ID);
        String userRealName = LoginUserHelper.getCurrentUserRealName().orElse(IConstants.DEFAULT_USER_USERNAME);
        entity.setUpdateUserId(userId);
        entity.setUpdateUserName(userRealName);
        entity.setUpdateTime(new Date());
    }

}
