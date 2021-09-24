package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.ReturnList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.query.ReturnListQuery;

/**
 * <p>
 * 退货单表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
public interface ReturnListMapper extends BaseMapper<ReturnList> {

    String getNextReturnNumber();

    IPage<ReturnList> listReturnList(IPage<ReturnList> page, ReturnListQuery rlQuery);

}
