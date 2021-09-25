package com.tang.admin.service;

import com.tang.admin.pojo.DamageList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.pojo.DamageListGoods;
import com.tang.admin.query.DamageListQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报损单表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
public interface IDamageListService extends IService<DamageList> {

    String getNextDamageNumber();

    void saveDamageList(String name, DamageList damageList, List<DamageListGoods> dlgList);

    Map<String, Object> listDamageList(DamageListQuery damageListQuery);

    void deleteDamageList(Integer id);

}
