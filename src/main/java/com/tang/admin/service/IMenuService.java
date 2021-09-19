package com.tang.admin.service;

import com.tang.admin.dto.TreeDto;
import com.tang.admin.pojo.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
public interface IMenuService extends IService<Menu> {

//    List<TreeDto> queryAllMenus(Integer roleId);

    Map<String, Object> menuList();

    Menu findMenuByNameAndGrade(String menuName, Integer grade);

    Menu findMenuByAclValue(String aclValue);

    Menu findMenuById(Integer id);

    Menu findMenuByUrlAndGrade(String url, Integer grade);

    void saveMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMenuById(Integer id);

    List<TreeDto> queryAllMenus(Integer roleId);
}
