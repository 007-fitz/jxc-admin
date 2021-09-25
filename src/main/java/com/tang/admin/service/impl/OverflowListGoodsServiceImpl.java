package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.OverflowListGoods;
import com.tang.admin.mapper.OverflowListGoodsMapper;
import com.tang.admin.query.OverflowListGoodsQuery;
import com.tang.admin.service.IOverflowListGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.PageResultUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 报溢单商品表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Service
public class OverflowListGoodsServiceImpl extends ServiceImpl<OverflowListGoodsMapper, OverflowListGoods> implements IOverflowListGoodsService {

    @Override
    public Map<String, Object> listOverflowListGoods(OverflowListGoodsQuery overflowListGoodsQuery) {
        IPage<OverflowListGoods> page = new Page<>(overflowListGoodsQuery.getPage(), overflowListGoodsQuery.getLimit());
        QueryWrapper<OverflowListGoods> queryWrapper = new QueryWrapper<>();
        if (overflowListGoodsQuery.getOverflowListId() != null) {
            queryWrapper.eq("overflow_list_id", overflowListGoodsQuery.getOverflowListId());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

}
