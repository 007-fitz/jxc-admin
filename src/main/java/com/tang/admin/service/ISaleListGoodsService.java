package com.tang.admin.service;

import com.tang.admin.pojo.SaleListGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.SaleListGoodsQuery;

import java.util.Map;

/**
 * <p>
 * 销售单商品表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
public interface ISaleListGoodsService extends IService<SaleListGoods> {

    Map<String, Object> listSaleListGoods(SaleListGoodsQuery saleListGoodsQuery);


}
