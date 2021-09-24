package com.tang.admin.mapper;

import com.tang.admin.pojo.dto.TreeDto;
import com.tang.admin.pojo.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-18
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<TreeDto> queryAllMenus();

}
