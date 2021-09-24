package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.PurchaseList;
import com.tang.admin.mapper.PurchaseListMapper;
import com.tang.admin.pojo.PurchaseListGoods;
import com.tang.admin.pojo.User;
import com.tang.admin.query.PurchaseListQuery;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.IPurchaseListGoodsService;
import com.tang.admin.service.IPurchaseListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.service.IUserService;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.DateUtil;
import com.tang.admin.utils.PageResultUtil;
import com.tang.admin.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 进货单 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Service
public class PurchaseListServiceImpl extends ServiceImpl<PurchaseListMapper, PurchaseList> implements IPurchaseListService {

    @Resource
    private IPurchaseListGoodsService purchaseListGoodsService;

    @Resource
    private IGoodsService goodsService;

    @Resource
    private IUserService userService;

    @Override
    public String getNextPurchaseNumber() {
        //JH20218070001X
        try {
            StringBuffer  stringBuffer = new StringBuffer();
            stringBuffer.append("JH");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String purchaseNumber = this.baseMapper.getNextPurchaseNumber();
            if(null!=purchaseNumber){
                stringBuffer.append(StringUtil.formatCode(purchaseNumber));
            }else {
                stringBuffer.append("0001");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void savePurchaseList(String username, PurchaseList purchaseList, List<PurchaseListGoods> plgList) {
        AssertUtil.isTrue(null == purchaseList.getSupplierId(), "请指定供应商");
        AssertUtil.isTrue(null == plgList, "请选择商品");

        purchaseList.setUserId(userService.findByUsername(username).getId());
        AssertUtil.isTrue(!this.save(purchaseList), "订单入库失败01");

        PurchaseList temp = this.getOne(new QueryWrapper<PurchaseList>().eq("purchase_number",purchaseList.getPurchaseNumber()));
        plgList.forEach(plg -> {
            plg.setPurchaseListId(temp.getId());
            // 入库会导致商品库存增加
            Goods goods = goodsService.getById(plg.getGoodsId());
            goods.setInventoryQuantity(goods.getInventoryQuantity() + plg.getNum());
            goods.setLastPurchasingPrice(plg.getPrice());
            goods.setState(2);
            goodsService.updateById(goods);
        });
        AssertUtil.isTrue(!purchaseListGoodsService.saveBatch(plgList), "订单入库失败02");
    }

    @Override
    public Map<String, Object> listPurchaseList(PurchaseListQuery purchaseListQuery) {
        IPage<PurchaseList> page = new Page<PurchaseList>(purchaseListQuery.getPage(), purchaseListQuery.getLimit());

        page = this.baseMapper.listPurchaseList(page, purchaseListQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }


}
