package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.PurchaseListGoods;
import com.tang.admin.mapper.PurchaseListGoodsMapper;
import com.tang.admin.query.PurchaseListGoodsQuery;
import com.tang.admin.service.IPurchaseListGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.PageResultUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 进货单商品表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Service
public class PurchaseListGoodsServiceImpl extends ServiceImpl<PurchaseListGoodsMapper, PurchaseListGoods> implements IPurchaseListGoodsService {

    /**
     * 展示进货订单中所包含的商品信息
     * @param plgQuery
     * @return
     */
    @Override
    public Map<String, Object> listPurchaseListGoods(PurchaseListGoodsQuery plgQuery) {
        IPage<PurchaseListGoods> page = new Page<>(plgQuery.getPage(), plgQuery.getLimit());
        QueryWrapper<PurchaseListGoods> queryWrapper = new QueryWrapper<>();
        if (plgQuery.getPurchaseListId() != null) {
            queryWrapper.eq("purchase_list_id", plgQuery.getPurchaseListId());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }
}
