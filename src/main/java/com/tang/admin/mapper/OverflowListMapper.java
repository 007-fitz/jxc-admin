package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.OverflowList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.query.OverFlowListQuery;

/**
 * <p>
 * 报溢单表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
public interface OverflowListMapper extends BaseMapper<OverflowList> {

    String getOverflowNumber(String prefixStr);

    IPage<OverflowList> listOverFlowList(IPage<OverflowList> page, OverFlowListQuery overFlowListQuery);

}
