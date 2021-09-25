package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.CustomerReturnList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.query.CustomerReturnListQuery;

/**
 * <p>
 * 客户退货单表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
public interface CustomerReturnListMapper extends BaseMapper<CustomerReturnList> {

    String getNextCustomerReturnNumber(String str);

    IPage<CustomerReturnList> listCustomerReturnList(IPage<CustomerReturnList> page, CustomerReturnListQuery customerReturnListQuery);

}
