package com.tang.admin.service;

import com.tang.admin.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-12
 */
public interface IUserService extends IService<User> {

    void updateUserInfo(User user);

    void updatePassword(Integer id, String oldPassword, String newPassword, String confirmPassword);

    User findByUsername(String username);

    User findById(Integer id);


}
