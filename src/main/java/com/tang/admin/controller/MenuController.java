package com.tang.admin.controller;


import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.Menu;
import com.tang.admin.service.IMenuService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
@Controller
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private IMenuService menuService;

    /**
     * 菜单展示 页面
     *
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "/menu/menu";
    }

    /**
     * 菜单展示
     *
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> menuList() {
        return menuService.menuList();
    }

    /**
     * 新增菜单记录 页面
     *
     * @param grade 中转
     * @param pId   中转
     * @param model 前面两个参数放在model中，以提供 freemarker 进行变量判断，从而选择性展示或选中
     * @return
     */
    @RequestMapping("/addMenuPage")
    public String addMenuPage(Integer grade, Integer pId, Model model) {
        model.addAttribute("grade", grade);
        model.addAttribute("pId", pId);
        return "/menu/add";
    }

    /**
     * 新增菜单
     *
     * @param menu
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean saveMenu(Menu menu) {
        menuService.saveMenu(menu);
        return RespBean.success("菜单添加成功");
    }

    /**
     * 更新菜单信息 页面
     *
     * @param id    菜单id 用于查找菜单信息
     * @param model 放置菜单信息
     * @return
     */
    @RequestMapping("/updateMenuPage")
    public String updateMenuPage(Integer id, Model model) {
        model.addAttribute("menu", menuService.getById(id));
        return "/menu/update";
    }

    /**
     * 更新菜单
     *
     * @param menu
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public RespBean updateMenu(Menu menu) {
        menuService.updateMenu(menu);
        return RespBean.success("菜单更新成功");
    }

    /**
     * 删除菜单
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        menuService.deleteMenuById(id);
        return RespBean.success("菜单记录删除成功");
    }


}
