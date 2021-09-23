package com.tang.admin.mapper;

import com.tang.admin.dto.TreeDto;
import com.tang.admin.pojo.GoodsType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品类别表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
public interface GoodsTypeMapper extends BaseMapper<GoodsType> {

    List<TreeDto> queryAllGoodsTypes(Integer typeId);

}
