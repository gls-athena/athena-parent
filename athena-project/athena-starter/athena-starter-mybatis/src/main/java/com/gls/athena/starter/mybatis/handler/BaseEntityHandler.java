package com.gls.athena.starter.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gls.athena.common.bean.security.LoginUserHelper;
import com.gls.athena.common.core.constant.IConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 基础实体处理器
 *
 * @author george
 */
@Slf4j
@Component
public class BaseEntityHandler implements MetaObjectHandler {

    /**
     * 插入填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 打印日志
        log.info("insertFill metaObject: {}", metaObject);

        // 获取当前用户信息和时间
        Long userId = LoginUserHelper.getCurrentUserId().orElse(IConstants.DEFAULT_USER_ID);
        String userName = LoginUserHelper.getCurrentUserRealName().orElse(IConstants.DEFAULT_USER_USERNAME);
        Date now = new Date();

        // 严格插入填充
        this.strictInsertFill(metaObject, "isDelete", Boolean.class, false);
        this.strictInsertFill(metaObject, "createUserId", Long.class, userId);
        this.strictInsertFill(metaObject, "createUserName", String.class, userName);
        this.strictInsertFill(metaObject, "createTime", Date.class, now);
        this.strictInsertFill(metaObject, "updateUserId", Long.class, userId);
        this.strictInsertFill(metaObject, "updateUserName", String.class, userName);
        this.strictInsertFill(metaObject, "updateTime", Date.class, now);
    }

    /**
     * 更新填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 打印日志
        log.info("updateFill metaObject: {}", metaObject);

        // 获取当前用户信息和时间
        Long userId = LoginUserHelper.getCurrentUserId().orElse(IConstants.DEFAULT_USER_ID);
        String userName = LoginUserHelper.getCurrentUserRealName().orElse(IConstants.DEFAULT_USER_USERNAME);
        Date now = new Date();

        // 严格更新填充用户ID、用户昵称和更新时间字段
        this.strictUpdateFill(metaObject, "updateUserId", Long.class, userId);
        this.strictUpdateFill(metaObject, "updateUserName", String.class, userName);
        this.strictUpdateFill(metaObject, "updateTime", Date.class, now);
    }

}
