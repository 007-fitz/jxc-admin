package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.CustomerReturnListGoods;
import com.tang.admin.mapper.CustomerReturnListGoodsMapper;
import com.tang.admin.query.CustomerReturnListGoodsQuery;
import com.tang.admin.service.ICustomerReturnListGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.PageResultUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 客户退货单商品表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Service
public class CustomerReturnListGoodsServiceImpl extends ServiceImpl<CustomerReturnListGoodsMapper, CustomerReturnListGoods> implements ICustomerReturnListGoodsService {

    /**
     * 查询客户退货订单中包含的商品信息
     * @param customerReturnListGoodsQuery
     * @return
     */
    @Override
    public Map<String, Object> listCustomerReturnListGoods(CustomerReturnListGoodsQuery customerReturnListGoodsQuery) {
        IPage<CustomerReturnListGoods> page = new Page<>(customerReturnListGoodsQuery.getPage(), customerReturnListGoodsQuery.getLimit());
        QueryWrapper<CustomerReturnListGoods> queryWrapper = new QueryWrapper<>();
        if (customerReturnListGoodsQuery.getCustomerReturnListId() != null) {
            queryWrapper.eq("customer_return_list_id", customerReturnListGoodsQuery.getCustomerReturnListId());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 根据商品id，查询此商品从客户退货总量
     * @param id
     * @return
     */
    @Override
    public Integer getReturnTotalByGoodId(Integer id) {
        CustomerReturnListGoods customerReturnListGoods = this.getOne(new QueryWrapper<CustomerReturnListGoods>().select("sum(num) as num").eq("goods_id",id));
        return null==customerReturnListGoods?0:customerReturnListGoods.getNum();
    }
}
