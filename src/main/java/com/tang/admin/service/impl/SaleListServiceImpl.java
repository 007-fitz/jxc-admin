package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.mapper.SaleListMapper;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.SaleList;
import com.tang.admin.pojo.SaleListGoods;
import com.tang.admin.query.SaleListQuery;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.ISaleListGoodsService;
import com.tang.admin.service.ISaleListService;
import com.tang.admin.service.IUserService;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.DateUtil;
import com.tang.admin.utils.PageResultUtil;
import com.tang.admin.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 销售单表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Service
public class SaleListServiceImpl extends ServiceImpl<SaleListMapper, SaleList> implements ISaleListService {

    @Resource
    private IUserService userService;

    @Resource
    private ISaleListGoodsService saleListGoodsService;

    @Resource
    private IGoodsService goodsService;

    /**
     * 生成销售订单编号
     *
     * @return
     */
    @Override
    public String getNextSaleNumber() {
        //JH20218070001X
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("XS");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String saleNumber = this.baseMapper.getNextSaleNumber(stringBuffer.toString());
            if (null != saleNumber) {
//                System.out.println(saleNumber.substring(saleNumber.length() - 4));
                stringBuffer.append(StringUtil.formatCode(saleNumber));
            } else {
                stringBuffer.append("0001");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 新建销售订单，同时保存订单中商品信息
     *
     * @param username
     * @param saleList
     * @param slgList
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveSaleList(String username, SaleList saleList, List<SaleListGoods> slgList) {
        int count = this.count(new QueryWrapper<SaleList>().eq("sale_number", saleList.getSaleNumber()));
        AssertUtil.isTrue(count != 0, "订单编号已存在");
        AssertUtil.isTrue(null == saleList.getCustomerId() || saleList.getCustomerId() == 0, "请指定客户");
        AssertUtil.isTrue(null == saleList.getSaleDate(), "请指定日期");
        AssertUtil.isTrue(null == slgList || slgList.size() == 0, "请指定商品");

        saleList.setUserId(userService.findByUsername(username).getId());
        AssertUtil.isTrue(!this.save(saleList), "销售订单创建失败01");

        SaleList temp = this.getOne(new QueryWrapper<SaleList>().eq("sale_number", saleList.getSaleNumber()));
        slgList.forEach(slg -> {
            slg.setSaleListId(temp.getId());
            // 同步更新商品库存
            Goods goods = goodsService.getById(slg.getGoodsId());
            int newInventory = goods.getInventoryQuantity() - slg.getNum();
            AssertUtil.isTrue(newInventory < 0, "商品 <" + goods.getName() + "> 库存不足");
            goods.setInventoryQuantity(newInventory);
            goods.setLastPurchasingPrice(slg.getPrice());
            goods.setState(2);
            goodsService.updateById(goods);
        });
        AssertUtil.isTrue(!saleListGoodsService.saveBatch(slgList), "销售订单创建失败02");
    }

    /**
     * 查询所有销售订单
     *
     * @param saleListQuery
     * @return 分页/条件性查询结果
     */
    @Override
    public Map<String, Object> listSaleList(SaleListQuery saleListQuery) {
        IPage<SaleList> page = new Page<SaleList>(saleListQuery.getPage(), saleListQuery.getLimit());
        page = this.baseMapper.listSaleList(page, saleListQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 删除销售订单，及其关联包含的商品信息
     * @param id
     */
    @Override
    public void deleteSaleList(Integer id) {
        AssertUtil.isTrue(!(saleListGoodsService.remove(new QueryWrapper<SaleListGoods>().eq("sale_list_id", id))), "记录删除失败");
        AssertUtil.isTrue(!(this.removeById(id)), "记录删除失败");
    }


}
