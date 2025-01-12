package com.gls.athena.security.servlet.client.social;

import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.common.bean.security.User;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 社交用户绑定监听器
 * <p>
 * 监听用户登录成功事件，处理社交账号与系统账号的绑定关系：
 * - 仅处理用户名密码方式的登录
 * - 检查会话中是否存在未绑定的社交账号
 * - 自动完成账号绑定
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Slf4j
@Component
public class SocialUserBindListener {
    /**
     * 社交用户服务
     */
    @Resource
    private ISocialUserService socialUserService;

    /**
     * 会话存储
     */
    @Resource
    private HttpSession session;

    /**
     * 处理认证成功事件
     * <p>
     * 检查并处理待绑定的社交账号，仅在以下条件都满足时执行绑定：
     * - 使用用户名密码方式登录成功
     * - 会话中存在未绑定的社交账号
     * </p>
     */
    @EventListener(AuthenticationSuccessEvent.class)
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            return;
        }

        SocialUser socialUser = (SocialUser) session.getAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY);
        if (socialUser == null || socialUser.isBindStatus()) {
            return;
        }

        try {
            User user = (User) authentication.getPrincipal();
            log.info("正在绑定社交用户，系统用户ID: {}, 社交平台: {}", user.getId(), socialUser.getRegistrationId());

            socialUser.setUser(user);
            socialUser.setBindStatus(true);
            socialUserService.saveSocialUser(socialUser);

            session.removeAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY);
            log.info("社交用户绑定成功，用户ID: {}", user.getId());
        } catch (Exception e) {
            log.error("社交用户绑定失败", e);
        }
    }
}
