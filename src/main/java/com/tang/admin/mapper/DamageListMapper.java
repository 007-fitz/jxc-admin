package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.DamageList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.query.DamageListQuery;

/**
 * <p>
 * 报损单表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
public interface DamageListMapper extends BaseMapper<DamageList> {

    String getNextDamageNumber(String prefixStr);

    IPage<DamageList> listDamageList(IPage<DamageList> page, DamageListQuery damageListQuery);

}
