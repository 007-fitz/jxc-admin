package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.admin.pojo.User;
import com.tang.admin.mapper.UserMapper;
import com.tang.admin.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.StringUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUserInfo(User user) {
        // 用户名不能为空
        // 用户名不能重复，当根据用户名查找 不为空 有记录，而id不是当前用户id时，用户名重复
        AssertUtil.isTrue(StringUtil.isEmpty(user.getUsername()), "用户名不能为空");
        User temp = this.findByUsername(user.getUsername());
        AssertUtil.isTrue(temp != null && !temp.getId().equals(user.getId()), "用户名已存在");
        AssertUtil.isTrue(!this.updateById(user), "用户更新失败");
    }


    @Override
    public void updatePassword(Integer id, String oldPassword, String newPassword, String confirmPassword) {
        /*
         * 三者不能为空，两次新密码需一致
         * 旧密码需正确，新旧密码不能重复
         */
        AssertUtil.isTrue(StringUtil.isEmpty(oldPassword), "请输入原始密码！");
        AssertUtil.isTrue(StringUtil.isEmpty(newPassword), "请输入新密码！");
        AssertUtil.isTrue(StringUtil.isEmpty(confirmPassword), "请输入确认密码！");
        AssertUtil.isTrue(!newPassword.equals(confirmPassword), "新密码两次不一致");
        User user = findById(id);
        AssertUtil.isTrue(!passwordEncoder.matches(oldPassword, user.getPassword()), "旧密码不正确");
        AssertUtil.isTrue(newPassword.equals(oldPassword), "新密码不能和旧密码一致");

        User newUser = new User();
        newUser.setId(id);
        newUser.setPassword(passwordEncoder.encode(newPassword));
        AssertUtil.isTrue(!this.updateById(newUser), "密码更新失败");
    }


    @Override
    public User findByUsername(String username) {
        return this.baseMapper.selectOne(new QueryWrapper<User>().eq("is_del", 0).eq("user_name", username));
    }

    @Override
    public User findById(Integer id) {
        return this.baseMapper.selectById(id);
    }
}
