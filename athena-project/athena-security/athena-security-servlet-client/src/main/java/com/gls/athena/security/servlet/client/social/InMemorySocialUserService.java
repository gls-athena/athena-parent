package com.gls.athena.security.servlet.client.social;

import com.gls.athena.common.bean.security.SocialUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 社交用户服务
 *
 * @author george
 */
public class InMemorySocialUserService implements ISocialUserService {

    /**
     * 用户列表
     */
    private static final List<SocialUser> USERS = new ArrayList<>();

    /**
     * 根据社交平台id和社交平台用户id查询社交用户
     *
     * @param registrationId 社交平台应用id
     * @param name           社交平台用户id
     * @return 社交用户
     */
    @Override
    public SocialUser loadSocialUser(String registrationId, String name) {
        return USERS.stream()
                .filter(user -> user.getRegistrationId().equals(registrationId) && user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 保存社交用户
     *
     * @param socialUser 社交用户
     * @return
     */
    @Override
    public SocialUser saveSocialUser(SocialUser socialUser) {
        USERS.removeIf(user -> user.getRegistrationId().equals(socialUser.getRegistrationId()) && user.getName().equals(socialUser.getName()));
        USERS.add(socialUser);
        return socialUser;
    }
}
