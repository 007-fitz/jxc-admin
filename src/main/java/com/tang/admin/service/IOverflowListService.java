package com.tang.admin.service;

import com.tang.admin.pojo.OverflowList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.pojo.OverflowListGoods;
import com.tang.admin.query.OverFlowListQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报溢单表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
public interface IOverflowListService extends IService<OverflowList> {

    Object getOverflowNumber();

    void saveOverflowList(String name, OverflowList overflowList, List<OverflowListGoods> olgList);

    Map<String, Object> listOverFlowList(OverFlowListQuery overFlowListQuery);

    void deleteOverflowList(Integer id);

}
