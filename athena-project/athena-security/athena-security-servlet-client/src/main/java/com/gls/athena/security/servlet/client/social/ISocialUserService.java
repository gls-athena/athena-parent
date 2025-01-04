package com.gls.athena.security.servlet.client.social;

import com.gls.athena.common.bean.security.SocialUser;

/**
 * 社交用户服务
 *
 * @author george
 */
public interface ISocialUserService {

    /**
     * 根据社交平台id和社交平台用户id查询社交用户
     *
     * @param registrationId 社交平台应用id
     * @param name           社交平台用户id
     * @return 社交用户
     */
    SocialUser loadSocialUser(String registrationId, String name);

    /**
     * 保存社交用户
     *
     * @param socialUser 社交用户
     * @return
     */
    SocialUser saveSocialUser(SocialUser socialUser);
}
