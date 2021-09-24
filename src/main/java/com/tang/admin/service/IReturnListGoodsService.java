package com.tang.admin.service;

import com.tang.admin.pojo.ReturnListGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.ReturnListGoodsQuery;

import java.util.Map;

/**
 * <p>
 * 退货单商品表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
public interface IReturnListGoodsService extends IService<ReturnListGoods> {

    Map<String, Object> listReturnListGoods(ReturnListGoodsQuery returnListGoodsQuery);

}
