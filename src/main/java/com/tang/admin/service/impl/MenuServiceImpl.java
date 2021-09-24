package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tang.admin.dto.TreeDto;
import com.tang.admin.pojo.Menu;
import com.tang.admin.mapper.MenuMapper;
import com.tang.admin.pojo.RoleMenu;
import com.tang.admin.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.service.IRoleMenuService;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.PageResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    /**
     * 菜单展示
     * @return
     */
    @Override
    public Map<String, Object> menuList() {
        List<Menu> menus = this.list(new QueryWrapper<Menu>().eq("is_del", 0));
        return PageResultUtil.getResult((long) menus.size(), menus);
    }

    /**
     * 新增菜单记录
     * @param menu
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveMenu(Menu menu) {
        /**
         * 1.名称校验
         *      菜单名不能为空 菜单层级(0,1,2）只支持3级菜单
         * 2.权限码
         *      非空 全局唯一
         * 3.同一层级下
         *      菜单名不能重复
         * 4.上级菜单
         *      上级菜单 必须存在
         * 5.url判断
         *      菜单属于二级菜单  url 不可重复
         * 6.设置参数
         *      是否删除 isdel state
         */
        Integer grade = menu.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 0 || grade == 1 || grade == 2), "菜单层级不合法!");
        AssertUtil.isTrue(null == menu.getPId() || null == this.findMenuById(menu.getPId()), "请指定上级菜单!");
        AssertUtil.isTrue(StringUtils.isBlank(menu.getName()), "菜单名不能为空");
        AssertUtil.isTrue(null != this.findMenuByNameAndGrade(menu.getName(), menu.getGrade()), "该层级下 菜单名称已存在!");
        AssertUtil.isTrue(null != this.findMenuByAclValue(menu.getAclValue()), "权限码已存在!");
        if (grade == 1) {
            // 二级菜单
            AssertUtil.isTrue(null != this.findMenuByUrlAndGrade(menu.getUrl(), menu.getGrade()), "该层级下url 不可重复!");
        }

        menu.setIsDel(0);
        menu.setState(0);
        AssertUtil.isTrue(!(this.save(menu)), "菜单添加失败!");
    }

    /**
     * 更新菜单记录
     * @param menu
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateMenu(Menu menu) {
        /**
         * 1.参数校验
         *    待更新记录必须存在
         *    菜单名 不能为空
         *    菜单层级 grade (0,1,2) 只支持三级菜单
         * 2.同一层级下
         *     菜单名不可重复
         * 3.权限码
         *    非空
         *    全局唯一
         * 4.上级菜单
         *    上级菜单 必须存在
         * 5.url 判断
         *      菜单属于二级菜单  url 不可重复的
         */
        AssertUtil.isTrue(null == menu.getId() || null == this.findMenuById(menu.getId()), "待更新的记录不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(menu.getName()), "菜单名不能为空!");
        Integer grade = menu.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 0 || grade == 1 || grade == 2), "菜单层级不合法!");
        Menu temp = this.findMenuByNameAndGrade(menu.getName(), menu.getGrade());
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(menu.getId())), "该层级下菜单已存在!");
        temp = this.findMenuByAclValue(menu.getAclValue());
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(menu.getId())), "权限码已存在!");
        AssertUtil.isTrue(null == menu.getPId() || null == this.findMenuById(menu.getPId()), "请指定上级菜单!");
        if (grade == 1) {
            temp = this.findMenuByUrlAndGrade(menu.getUrl(), menu.getGrade());
            AssertUtil.isTrue(null != temp && !(temp.getId().equals(menu.getId())), "该层级下url 不可重复!");
        }
        AssertUtil.isTrue(!(this.updateById(menu)), "菜单记录更新失败!");
    }

    @Override
    public Menu findMenuByNameAndGrade(String menuName, Integer grade) {
        return this.getOne(new QueryWrapper<Menu>().eq("is_del", 0).eq("name", menuName).eq("grade", grade));
    }

    @Override
    public Menu findMenuByAclValue(String aclValue) {
        return this.getOne(new QueryWrapper<Menu>().eq("is_del", 0).eq("acl_value", aclValue));
    }

    @Override
    public Menu findMenuById(Integer id) {
        return this.getOne(new QueryWrapper<Menu>().eq("is_del", 0).eq("id", id));
    }

    @Override
    public Menu findMenuByUrlAndGrade(String url, Integer grade) {
        return this.getOne(new QueryWrapper<Menu>().eq("is_del", 0).eq("url", url).eq("grade", grade));
    }

    /**
     * 删除菜单记录
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteMenuById(Integer id) {
        /**
         * 1.参数校验
         *    记录必须存在
         * 2.子菜单
         *   如果存在子菜单  不允许直接删除上级菜单
         * 3.角色菜单表关联
         *     将关联的角色菜单记录一并删除
         * 4.执行菜单记录删除
         */
        AssertUtil.isTrue(null==id || null==this.findMenuById(id), "待删除的记录不存在!");
        int count = this.count(new QueryWrapper<Menu>().eq("is_del", 0).eq("p_id", id));
        AssertUtil.isTrue(count>0, "存在子菜单，不允许直接删除!");
        count = roleMenuService.count(new QueryWrapper<RoleMenu>().eq("menu_id",id));
        if(count>0){
            AssertUtil.isTrue(!(roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("menu_id",id))),"菜单删除失败!");
        }
        Menu menu = new Menu();
        menu.setId(id);
        menu.setIsDel(1);
        AssertUtil.isTrue(!(this.updateById(menu)),"菜单删除失败!");
    }



    @Resource
    private IRoleMenuService roleMenuService;

    /**
     * 层级展示所有权限，同时如果id非空，进行回显
     * @param roleId 当非空时，将checked属性设置为真，进行回显
     * @return
     */
    @Override
    public List<TreeDto> queryAllMenus(Integer roleId) {
        List<TreeDto> treeDtos = this.baseMapper.queryAllMenus();
        List<Integer> roleHasMenuIds = roleMenuService.queryRoleHasAllMenus(roleId);
        if (!CollectionUtils.isEmpty(roleHasMenuIds)){
            treeDtos.forEach(treeDto -> {
                if(roleHasMenuIds.contains(treeDto.getId())){
                    treeDto.setChecked(true);
                }
            });
        }
        return treeDtos;
    }

}
