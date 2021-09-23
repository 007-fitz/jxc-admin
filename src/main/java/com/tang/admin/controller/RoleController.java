package com.tang.admin.controller;


import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.Role;
import com.tang.admin.query.RoleQuery;
import com.tang.admin.service.IRoleService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
@Controller
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;

    /**
     * 角色列表展示 页面
     *
     * @return
     */
    @RequestMapping("/index")
    public String queryAllRoles() {
        return "/role/role";
    }

    /**
     * 角色列表展示
     *
     * @param roleQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(RoleQuery roleQuery) {
        return roleService.listRoles(roleQuery);
    }

    /**
     * 角色添加/更新 页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/addOrUpdateRolePage")
    public String addOrUpdateRolePage(Integer id, Model model) {
        if (null != id) {
            model.addAttribute("role", roleService.getById(id));
        }
        return "/role/add_update";
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Role role) {
        roleService.saveRole(role);
        return RespBean.success("角色添加成功");
    }

    /**
     * 更新角色信息
     *
     * @param role
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(Role role) {
        roleService.updateRole(role);
        return RespBean.success("角色更新成功");
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        roleService.deleteRole(id);
        return RespBean.success("角色记录删除成功");
    }

    /**
     * 授权页面展示
     * @return
     */
    @RequestMapping("/toAddGrantPage")
    public String toAddGrantPage(Integer roleId, Model model) {
        model.addAttribute("roleId", roleId);
        return "role/grant";
    }

    /**
     * 用户-权限 关系建立
     * @param roleId
     * @param mids
     * @return
     */
    @RequestMapping("/addGrant")
    @ResponseBody
    public RespBean addGrant(Integer roleId, Integer[] mids) {
        roleService.addGrant(roleId, mids);
        return RespBean.success("角色-权限关系建立成功");
    }

    /**
     * 角色简约展示，对于特定的用户，当已存在用户角色关系时，经进行回显标记
     * @param userId
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

}
