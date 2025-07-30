package com.gls.athena.security.core.jackson2;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.security.web.csrf.DefaultCsrfToken;

/**
 * Spring Security 核心Jackson序列化模块
 * 用于处理安全相关对象的JSON序列化和反序列化
 *
 * @author george
 * @since 1.0.0
 */
public class CoreSecurityModule extends SimpleModule {
    /**
     * 初始化SecurityModule
     * 设置模块名称和版本信息
     */
    public CoreSecurityModule() {
        super(CoreSecurityModule.class.getName(), new Version(1, 0, 0, null, "com.gls.athena.security", "athena-security-core"));
    }

    /**
     * 配置模块的序列化和反序列化行为
     *
     * @param context 模块设置上下文，用于注册MixIn注解
     */
    @Override
    public void setupModule(SetupContext context) {
        // 注册DefaultCsrfToken的MixIn，用于自定义CSRF令牌的序列化
        context.setMixInAnnotations(DefaultCsrfToken.class, CsrfTokenMixin.class);
    }
}
