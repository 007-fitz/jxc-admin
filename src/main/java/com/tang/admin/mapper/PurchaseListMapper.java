package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.PurchaseList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.query.PurchaseListQuery;

/**
 * <p>
 * 进货单 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
public interface PurchaseListMapper extends BaseMapper<PurchaseList> {

    String getNextPurchaseNumber();

    IPage<PurchaseList> listPurchaseList(IPage<PurchaseList> page, PurchaseListQuery plQuery);

}
