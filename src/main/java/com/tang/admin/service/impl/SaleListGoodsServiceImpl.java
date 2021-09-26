package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.SaleListGoods;
import com.tang.admin.mapper.SaleListGoodsMapper;
import com.tang.admin.query.SaleListGoodsQuery;
import com.tang.admin.service.ISaleListGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.PageResultUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 销售单商品表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Service
public class SaleListGoodsServiceImpl extends ServiceImpl<SaleListGoodsMapper, SaleListGoods> implements ISaleListGoodsService {

    /**
     * 条件性查询销售订单中包含的商品
     * @param saleListGoodsQuery
     * @return
     */
    @Override
    public Map<String, Object> listSaleListGoods(SaleListGoodsQuery saleListGoodsQuery) {
        IPage<SaleListGoods> page = new Page<SaleListGoods>(saleListGoodsQuery.getPage(), saleListGoodsQuery.getLimit());
        QueryWrapper<SaleListGoods> queryWrapper = new QueryWrapper<>();
        if (saleListGoodsQuery.getSaleListId() != null) {
            queryWrapper.eq("sale_list_id", saleListGoodsQuery.getSaleListId());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 根据商品id，查询此商品销售总量
     * @param id
     * @return
     */
    @Override
    public Integer getSaleTotalByGoodId(Integer id) {
        SaleListGoods saleListGoods = this.getOne(new QueryWrapper<SaleListGoods>().select("sum(num) as num").eq("goods_id",id));
        System.out.println(saleListGoods);
        return null==saleListGoods?0:saleListGoods.getNum();
    }

}
