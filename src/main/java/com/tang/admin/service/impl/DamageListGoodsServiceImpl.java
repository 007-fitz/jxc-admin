package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.DamageListGoods;
import com.tang.admin.mapper.DamageListGoodsMapper;
import com.tang.admin.query.DamageListGoodsQuery;
import com.tang.admin.service.IDamageListGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.PageResultUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 报损单商品表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Service
public class DamageListGoodsServiceImpl extends ServiceImpl<DamageListGoodsMapper, DamageListGoods> implements IDamageListGoodsService {

    /**
     * 报损单中商品信息的条件性展示
     * @param damageListGoodsQuery
     * @return
     */
    @Override
    public Map<String, Object> listDamageListGoods(DamageListGoodsQuery damageListGoodsQuery) {
        IPage<DamageListGoods> page = new Page<>(damageListGoodsQuery.getPage(), damageListGoodsQuery.getLimit());
        QueryWrapper<DamageListGoods> queryWrapper = new QueryWrapper<>();
        if (damageListGoodsQuery.getDamageListId() != null) {
            queryWrapper.eq("damage_list_id", damageListGoodsQuery.getDamageListId());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }
}
