package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.admin.pojo.*;
import com.tang.admin.mapper.PurchaseListMapper;
import com.tang.admin.pojo.model.CountResultModel;
import com.tang.admin.query.PurchaseListQuery;
import com.tang.admin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    @Resource
    private IGoodsTypeService goodsTypeService;

    /**
     * 生成进货订单编号
     *
     * @return
     */
    @Override
    public String getNextPurchaseNumber() {
        //JH20218070001X
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("JH");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String purchaseNumber = this.baseMapper.getNextPurchaseNumber(stringBuffer.toString());
            if (null != purchaseNumber) {
                stringBuffer.append(StringUtil.formatCode(purchaseNumber));
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
     * 保存进货订单信息
     *
     * @param username     当前用户用户名
     * @param purchaseList 订单信息
     * @param plgList      订单所包含的商品信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void savePurchaseList(String username, PurchaseList purchaseList, List<PurchaseListGoods> plgList) {
        int count = this.count(new QueryWrapper<PurchaseList>().eq("purchase_number", purchaseList.getPurchaseNumber()));
        AssertUtil.isTrue(count != 0, "订单编号已存在");
        AssertUtil.isTrue(null == purchaseList.getSupplierId() || purchaseList.getSupplierId() == 0, "请指定供应商");
        AssertUtil.isTrue(null == purchaseList.getPurchaseDate(), "请指定日期");
        AssertUtil.isTrue(null == plgList, "请选择商品");

        purchaseList.setUserId(userService.findByUsername(username).getId());
        AssertUtil.isTrue(!this.save(purchaseList), "订单入库失败01");

        PurchaseList temp = this.getOne(new QueryWrapper<PurchaseList>().eq("purchase_number", purchaseList.getPurchaseNumber()));
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

    /**
     * 条件性展示进货订单信息
     *
     * @param purchaseListQuery
     * @return 分页查询结果
     */
    @Override
    public Map<String, Object> listPurchaseList(PurchaseListQuery purchaseListQuery) {
        IPage<PurchaseList> page = new Page<PurchaseList>(purchaseListQuery.getPage(), purchaseListQuery.getLimit());
        page = this.baseMapper.listPurchaseList(page, purchaseListQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 删除进货订单，同时删除订单所关联的商品信息
     *
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePurchaseList(Integer id) {
        AssertUtil.isTrue(!purchaseListGoodsService.remove(new QueryWrapper<PurchaseListGoods>().eq("purchase_list_id", id)), "进货订单删除失败01");
        AssertUtil.isTrue(!this.removeById(id), "进货订单删除失败02");
    }

    /**
     * 更新单据状态为已支付
     *
     * @param pid
     */
    @Override
    public void updatePurchaseList(Integer pid) {
        PurchaseList purchaseList = this.getById(pid);
        AssertUtil.isTrue(null == purchaseList, "待结算的记录不存在");
        AssertUtil.isTrue(purchaseList.getState() == 1, "记录已支付");
        purchaseList.setState(1);
        AssertUtil.isTrue(!(this.updateById(purchaseList)), "记录结算失败");
    }

    @Override
    public Map<String, Object> countPurchase(PurchaseListQuery purchaseListQuery) {
        if(null!=purchaseListQuery.getTypeId()){
            List<Integer> typeIds = goodsTypeService.queryAllSubTypeIdsByTypeId(purchaseListQuery.getTypeId());
            purchaseListQuery.setTypeIds(typeIds);
        }
        purchaseListQuery.setIndex((purchaseListQuery.getPage()-1)*purchaseListQuery.getLimit());
        Long count = this.baseMapper.countPurchaseTotal(purchaseListQuery);
        List<CountResultModel> list = this.baseMapper.countPurchaseList(purchaseListQuery);
        return PageResultUtil.getResult(count,list);
    }
}
