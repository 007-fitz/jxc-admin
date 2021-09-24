package com.tang.admin.service.impl;

import com.tang.admin.service.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class IRbacServiceImpl implements IRbacService {

    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private IRoleMenuService roleMenuService;

    /**
     * 依据用户名，查找出该用户拥有的所有角色
     * @param username 用户名
     * @return 角色名集合
     */
    @Override
    public List<String> findRolesByUserName(String username) {
        return userRoleService.findRolesByUserName(username);
    }

    /**
     * 根据角色，查找出所有权限
     * @param roleNames 多个角色名，List集合形式
     * @return 权限名集合
     */
    @Override
    public List<String> findAuthoritiesByRoleName(List<String> roleNames) {
        return roleMenuService.findAuthoritiesByRoleName(roleNames);
    }
}
