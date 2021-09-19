package com.tang.admin.service;

import com.tang.admin.pojo.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色菜单表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    List<Integer> queryRoleHasAllMenus(Integer roleId);

    List<String> findAuthoritiesByRoleName(List<String> roleNames);
}
