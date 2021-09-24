package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.ReturnListGoods;
import com.tang.admin.mapper.ReturnListGoodsMapper;
import com.tang.admin.query.ReturnListGoodsQuery;
import com.tang.admin.service.IReturnListGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.PageResultUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 退货单商品表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Service
public class ReturnListGoodsServiceImpl extends ServiceImpl<ReturnListGoodsMapper, ReturnListGoods> implements IReturnListGoodsService {


    /**
     * 出库订单中包含的商品的展示
     * @param returnListGoodsQuery
     * @return
     */
    @Override
    public Map<String, Object> listReturnListGoods(ReturnListGoodsQuery returnListGoodsQuery) {
        IPage<ReturnListGoods> page = new Page<ReturnListGoods>(returnListGoodsQuery.getPage(), returnListGoodsQuery.getLimit());
        QueryWrapper<ReturnListGoods> queryWrapper = new QueryWrapper<>();
        if (returnListGoodsQuery.getReturnListId() != null) {
            queryWrapper.eq("return_list_id", returnListGoodsQuery.getReturnListId());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }
}
