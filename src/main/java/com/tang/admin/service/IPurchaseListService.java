package com.tang.admin.service;

import com.tang.admin.pojo.PurchaseList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.pojo.PurchaseListGoods;
import com.tang.admin.query.PurchaseListQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 进货单 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
public interface IPurchaseListService extends IService<PurchaseList> {

    String getNextPurchaseNumber();

    void savePurchaseList(String username, PurchaseList purchaseList, List<PurchaseListGoods> plgList);

    Map<String, Object> listPurchaseList(PurchaseListQuery purchaseListQuery);

}
