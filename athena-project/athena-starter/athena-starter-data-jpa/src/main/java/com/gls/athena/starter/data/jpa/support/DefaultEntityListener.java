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
     * 填充创建人、创建时间、更新人、更新时间等审计字段
     *
     * @param entity 待保存的实体对象
     */
    @PrePersist
    public void prePersist(BaseEntity entity) {
        log.debug("Entity pre persist processing: [{}]", entity.getClass().getSimpleName());

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
     * 实体更新前的处理
     * 更新修改人和修改时间字段
     *
     * @param entity 待更新的实体对象
     */
    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        log.debug("Entity pre update processing: [{}]", entity.getClass().getSimpleName());

        // 获取当前用户信息并更新审计字段
        Long userId = LoginUserHelper.getCurrentUserId().orElse(IConstants.DEFAULT_USER_ID);
        String userRealName = LoginUserHelper.getCurrentUserRealName().orElse(IConstants.DEFAULT_USER_USERNAME);
        entity.setUpdateUserId(userId);
        entity.setUpdateUserName(userRealName);
        entity.setUpdateTime(new Date());
    }
}
