package com.tang.admin.service;

import com.tang.admin.pojo.OverflowListGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.OverflowListGoodsQuery;

import java.util.Map;

/**
 * <p>
 * 报溢单商品表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
public interface IOverflowListGoodsService extends IService<OverflowListGoods> {

    Map<String, Object> listOverflowListGoods(OverflowListGoodsQuery overflowListGoodsQuery);

}
