package com.tang.admin.service;

import com.tang.admin.pojo.PurchaseListGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.PurchaseListGoodsQuery;

import java.util.Map;

/**
 * <p>
 * 进货单商品表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
public interface IPurchaseListGoodsService extends IService<PurchaseListGoods> {

    Map<String, Object> listPurchaseListGoods(PurchaseListGoodsQuery purchaseListGoodsQuery);

}
