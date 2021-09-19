package com.tang.admin.service.impl;

import com.tang.admin.pojo.UserRole;
import com.tang.admin.mapper.UserRoleMapper;
import com.tang.admin.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Override
    public List<String> findRolesByUserName(String username) {
        return this.baseMapper.findRolesByUserName(username);
    }
}
