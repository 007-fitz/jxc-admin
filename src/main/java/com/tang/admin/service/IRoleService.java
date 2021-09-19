package com.tang.admin.service;

import com.tang.admin.pojo.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.RoleQuery;

import java.util.Map;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
public interface IRoleService extends IService<Role> {

    Map<String, Object> listRoles(RoleQuery roleQuery);

    void saveRole(Role role);

    void updateRole(Role role);

    void deleteRole(Integer id);

}
