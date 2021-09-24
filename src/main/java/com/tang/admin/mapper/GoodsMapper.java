package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.query.GoodsQuery;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    IPage<Goods> queryGoodsByParams(IPage<Goods> page, GoodsQuery goodsQuery);

    Goods getGoodsInfoById(Integer gid);

}
