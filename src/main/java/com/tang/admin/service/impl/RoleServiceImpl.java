package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.Role;
import com.tang.admin.mapper.RoleMapper;
import com.tang.admin.pojo.RoleMenu;
import com.tang.admin.query.RoleQuery;
import com.tang.admin.service.IRoleMenuService;
import com.tang.admin.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.PageResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private IRoleMenuService roleMenuService;

    /**
     * 根据条件列出角色
     *
     * @param roleQuery
     * @return
     */
    @Override
    public Map<String, Object> listRoles(RoleQuery roleQuery) {
        IPage<Role> page = new Page<>(roleQuery.getPage(), roleQuery.getLimit());
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", 0);
        if (StringUtils.isNotBlank(roleQuery.getRoleName())) {
            queryWrapper.like("name", roleQuery.getRoleName());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 根据角色名查找角色
     *
     * @param roleName
     * @return
     */
    private Role findRoleByName(String roleName) {
        return this.baseMapper.selectOne(new QueryWrapper<Role>().eq("is_del", 0).eq("name", roleName));
    }

    /**
     * 保存角色
     *
     * @param role
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveRole(Role role) {
        /*
        角色名非空
        角色名不可重复
         */
        AssertUtil.isTrue(StringUtils.isBlank(role.getName()), "角色名不能为空");
        AssertUtil.isTrue(null != this.findRoleByName(role.getName()), "角色名已存在");
        role.setIsDel(0);
        AssertUtil.isTrue(!(this.save(role)), "角色添加失败");
    }

    /**
     * 更新角色信息
     *
     * @param role
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateRole(Role role) {
        /*
        角色名非空
        角色名不可重复，除非未修改
         */
        AssertUtil.isTrue(StringUtils.isBlank(role.getName()), "角色名不能为空");
        Role temp = this.findRoleByName(role.getName());
        AssertUtil.isTrue(null != temp && !role.getId().equals(temp.getId()), "角色名已存在");
        AssertUtil.isTrue(!this.updateById(role), "角色更新失败");
    }

    /**
     * 根据id删除角色
     *
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteRole(Integer id) {
        AssertUtil.isTrue(null == id || null == this.getById(id), "待删除角色不存在");
        Role role = new Role();
        role.setId(id);
        role.setIsDel(1);
        AssertUtil.isTrue(!this.updateById(role), "角色记录删除失败");
    }

    /**
     * 查询所有角色接口
     * 在指定uid的基础上，标出该用户所拥有的角色。
     * (通过sql语句实现，先查出该用户所拥有角色，作为临时表，然后临时表与自身完整表自左联，对于null的标记未selected，供前端处理)
     * (也可以分两步，分两个集合（每个记录为Map），先查出所有角色 集合1，然后查出该用户对应角色 集合2，对于共有的角色，在集合1中标记上selected（map当中加key-value）)
     *
     * @param userId 用户id
     * @return (( roleName ， id ， selected)->Map ) ->List
     */
    @Override
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return this.baseMapper.queryAllRoles(userId);
    }

    @Override
    public void addGrant(Integer roleId, Integer[] mids) {
        AssertUtil.isTrue(null == roleId || null == this.getById(roleId), "角色不存在");
        int count = roleMenuService.count(new QueryWrapper<RoleMenu>().eq("role_id", roleId));
        if (count > 0) {
            AssertUtil.isTrue(!(roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id", roleId))), "角色授权失败");
        }
        if (null != mids && mids.length > 0) {
            List<RoleMenu> roleMenus = new ArrayList<RoleMenu>();
            for (Integer mid : mids) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(mid);
                roleMenus.add(roleMenu);
            }
            AssertUtil.isTrue(!(roleMenuService.saveBatch(roleMenus)), "角色授权失败");
        }
    }
}
