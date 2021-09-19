package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.User;
import com.tang.admin.mapper.UserMapper;
import com.tang.admin.pojo.UserRole;
import com.tang.admin.query.UserQuery;
import com.tang.admin.service.IUserRoleService;
import com.tang.admin.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.PageResultUtil;
import com.tang.admin.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    @Resource
    private IUserRoleService userRoleService;

    /**
     * 当前用户信息修改
     *
     * @param user
     */
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

    /**
     * 当前用户密码修改
     *
     * @param id              用户id
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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

    /**
     * 根据用户名查找用户
     *
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        return this.baseMapper.selectOne(new QueryWrapper<User>().eq("is_del", 0).eq("user_name", username));
    }

    /**
     * 根据用户id查找用户
     *
     * @param id
     * @return
     */
    @Override
    public User findById(Integer id) {
        return this.baseMapper.selectById(id);
    }

    /**
     * 用户信息展条件性展示
     *
     * @param userQuery 分页参数，可能有用户名模糊查询
     * @return layui渲染表格所需要的json格式
     */
    @Override
    public Map<String, Object> userList(UserQuery userQuery) {
        IPage<User> page = new Page<User>(userQuery.getPage(), userQuery.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", 0);
        if (StringUtils.isNotBlank(userQuery.getUserName())) {
            queryWrapper.like("user_name", userQuery.getUserName());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveUser(User user) {
        /*
        用户名非空
        用户名不能重复
         */
        AssertUtil.isTrue(StringUtils.isBlank(user.getUsername()), "用户名不能为空");
        AssertUtil.isTrue(null != this.findByUsername(user.getUsername()), "用户名已存在");
        user.setPassword(passwordEncoder.encode("asdf"));
        user.setIsDel(0);
        AssertUtil.isTrue(!this.save(user), "用户保存失败");
        relationUserRole(this.findByUsername(user.getUsername()).getId(), user.getRoleIds());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUser(User user) {
        /*
        用户名非空
        用户名不能重复，除非没改还是自身
         */
        AssertUtil.isTrue(StringUtils.isBlank(user.getUsername()), "用户名不能为空");
        User temp = this.findByUsername(user.getUsername());
        AssertUtil.isTrue(null != temp && !temp.getId().equals(user.getId()), "用户名已存在");
        AssertUtil.isTrue(!this.updateById(user), "用户更新失败");
        relationUserRole(user.getId(), user.getRoleIds());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUsers(Integer[] ids) {
        /*
        ids非空
        当存在用户角色关系时，用户不能删除。手动外键逻辑
         */
        AssertUtil.isTrue(null == ids || ids.length == 0, "请选择待删除的记录id!");
        int count = userRoleService.count(new QueryWrapper<UserRole>().in("user_id", Arrays.asList(ids)));
        AssertUtil.isTrue(count>0, "某些用户存在角色关系，不能删除");
        ArrayList<User> users = new ArrayList<>();
        for (Integer id : ids) {
            User user = new User();
            user.setId(id);
            user.setIsDel(1);
            users.add(user);
        }
        AssertUtil.isTrue(!this.updateBatchById(users), "用户删除失败");
    }

    /**
     * 维护用户与角色之间的关系。
     * 如果存在旧记录，直接删除，以新的为准
     *
     * @param uid  用户id
     * @param rids 角色id，字符串形式，以 , 分隔
     */
    public void relationUserRole(Integer uid, String rids) {
        int count = userRoleService.count(new QueryWrapper<UserRole>().eq("user_id", uid));
        if (count > 0) {
            AssertUtil.isTrue(!(userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", uid))), "用户角色分配异常01");
        }
        if (StringUtils.isNotBlank(rids)) {
            ArrayList<UserRole> userRoles = new ArrayList<>();
            for (String rid : rids.split(",")) {
                UserRole userRole = new UserRole();
                userRole.setUserId(uid);
                userRole.setRoleId(Integer.parseInt(rid));
                userRoles.add(userRole);
            }
            AssertUtil.isTrue(!userRoleService.saveBatch(userRoles), "用户角色分配异常02");
        }
    }


}
