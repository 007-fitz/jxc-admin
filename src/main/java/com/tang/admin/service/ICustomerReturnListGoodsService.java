package com.tang.admin.service;

import com.tang.admin.pojo.CustomerReturnListGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.CustomerReturnListGoodsQuery;

import java.util.Map;

/**
 * <p>
 * 客户退货单商品表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
public interface ICustomerReturnListGoodsService extends IService<CustomerReturnListGoods> {

    Map<String, Object> listCustomerReturnListGoods(CustomerReturnListGoodsQuery customerReturnListGoodsQuery);

    Integer getReturnTotalByGoodId(Integer id);

}
