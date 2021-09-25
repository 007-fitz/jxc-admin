package com.tang.admin.service;

import com.tang.admin.pojo.CustomerReturnList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.pojo.CustomerReturnListGoods;
import com.tang.admin.query.CustomerReturnListQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户退货单表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
public interface ICustomerReturnListService extends IService<CustomerReturnList> {

    String getNextCustomerReturnNumber();

    void saveCustomerReturnList(String name, CustomerReturnList customerReturnList, List<CustomerReturnListGoods> crlgList);

    Map<String, Object> listCustomerReturnList(CustomerReturnListQuery customerReturnListQuery);

    void deleteSaleList(Integer id);

}
