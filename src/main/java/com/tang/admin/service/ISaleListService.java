package com.tang.admin.service;

import com.tang.admin.pojo.PurchaseListGoods;
import com.tang.admin.pojo.SaleList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.pojo.SaleListGoods;
import com.tang.admin.query.SaleListQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 销售单表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
public interface ISaleListService extends IService<SaleList> {

    String getNextSaleNumber();

    void saveSaleList(String name, SaleList saleList, List<SaleListGoods> slgList);

    Map<String, Object> listSaleList(SaleListQuery saleListQuery);

    void deleteSaleList(Integer id);

}
