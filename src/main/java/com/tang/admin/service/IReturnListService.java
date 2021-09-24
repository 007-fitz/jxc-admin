package com.tang.admin.service;

import com.tang.admin.pojo.ReturnList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.pojo.ReturnListGoods;
import com.tang.admin.query.ReturnListQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 退货单表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
public interface IReturnListService extends IService<ReturnList> {

    String getNextReturnNumber();

    void saveReturnList(String name, ReturnList returnList, List<ReturnListGoods> rlgList);

    Map<String, Object> listReturnList(ReturnListQuery returnListQuery);

    void deleteReturnListService(Integer id);

}
