package com.gls.athena.security.servlet.authorization.support;

import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * 用户服务
 *
 * @author george
 */
public interface IUserService extends UserDetailsManager, UserDetailsPasswordService {
}
