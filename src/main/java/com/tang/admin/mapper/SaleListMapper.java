package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.SaleList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.query.SaleListQuery;

/**
 * <p>
 * 销售单表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
public interface SaleListMapper extends BaseMapper<SaleList> {

    String getNextSaleNumber(String str);

    IPage<SaleList> listSaleList(IPage<SaleList> page, SaleListQuery saleListQuery);

}
