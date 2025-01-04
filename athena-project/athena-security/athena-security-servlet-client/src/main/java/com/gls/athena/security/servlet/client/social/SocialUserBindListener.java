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
 *
 * @author george
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
     * 会话
     */
    @Resource
    private HttpSession session;

    /**
     * 绑定社交用户
     *
     * @param event 认证成功事件
     */
    @EventListener(AuthenticationSuccessEvent.class)
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        log.info("社交用户绑定监听器");
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
