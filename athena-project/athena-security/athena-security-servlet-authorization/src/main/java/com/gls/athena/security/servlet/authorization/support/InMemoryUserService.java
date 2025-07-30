package com.gls.athena.security.servlet.authorization.support;

import cn.hutool.core.collection.CollUtil;
import com.gls.athena.common.bean.security.LoginUserHelper;
import com.gls.athena.common.bean.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存用户服务
 *
 * @author george
 */
@Slf4j
public class InMemoryUserService implements IUserService {

    /**
     * 用户列表
     */
    private static final List<User> USERS = new ArrayList<>();

    /**
     * 构造方法
     *
     * @param users 用户列表
     */
    public InMemoryUserService(User... users) {
        CollUtil.addAll(USERS, users);
    }

    /**
     * 更新密码
     *
     * @param user        用户
     * @param newPassword 新密码
     *                    {@code PasswordEncoder} 加密后的密码
     * @return 用户
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return USERS.stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst()
                .map(u -> {
                    u.setPassword(newPassword);
                    return u;
                })
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    /**
     * 创建用户
     *
     * @param user 用户
     */
    @Override
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername())) {
            throw new IllegalArgumentException("用户已存在");
        }
        USERS.add((User) user);
    }

    /**
     * 更新用户
     *
     * @param user 用户
     */
    @Override
    public void updateUser(UserDetails user) {
        USERS.stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst()
                .ifPresent(u -> {
                    USERS.remove(u);
                    USERS.add((User) user);
                });
    }

    /**
     * 删除用户
     *
     * @param username 用户名
     */
    @Override
    public void deleteUser(String username) {
        USERS.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .ifPresent(USERS::remove);
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User user = (User) LoginUserHelper.getCurrentUser().orElseThrow(() -> new IllegalArgumentException("用户未登录"));
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("原密码错误");
        }
        USERS.stream()
                .filter(u -> u.getUsername().equals(user.getUsername()))
                .findFirst()
                .ifPresent(u -> u.setPassword(newPassword));
    }

    /**
     * 用户是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    @Override
    public boolean userExists(String username) {
        return USERS.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    /**
     * 通过用户名加载用户
     *
     * @param username 用户名
     * @return 用户
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return USERS.stream()
                // 用户名、手机号、邮箱
                .filter(user -> user.getUsername().equals(username) || user.getMobile().equals(username) || user.getEmail().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }
}
