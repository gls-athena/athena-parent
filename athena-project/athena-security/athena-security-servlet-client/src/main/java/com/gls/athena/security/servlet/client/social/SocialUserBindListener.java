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
 * 用于处理社交用户与系统用户的绑定关系
 *
 * @author george
 * @since 1.0.0
 */
@Slf4j
@Component
public class SocialUserBindListener {
    /**
     * 社交用户服务接口，用于处理社交用户的相关操作
     */
    @Resource
    private ISocialUserService socialUserService;

    /**
     * HTTP会话对象，用于存储社交用户临时信息
     */
    @Resource
    private HttpSession session;

    /**
     * 处理认证成功事件，完成社交用户与系统用户的绑定
     *
     * @param event 认证成功事件，包含认证成功的用户信息
     * @see AuthenticationSuccessEvent
     * @see UsernamePasswordAuthenticationToken
     */
    @EventListener(AuthenticationSuccessEvent.class)
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        log.info("开始处理社交用户绑定事件");
        Authentication authentication = event.getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            User user = (User) authentication.getPrincipal();
            SocialUser socialUser = (SocialUser) session.getAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY);
            if (socialUser != null) {
                // 绑定社交用户
                socialUser.setUser(user);
                socialUser.setBindStatus(true);
                socialUserService.saveSocialUser(socialUser);
                session.removeAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY);
                log.info("社交用户绑定成功");
            }
        }
    }
}
