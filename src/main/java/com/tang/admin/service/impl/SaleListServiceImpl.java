package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.mapper.SaleListMapper;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.SaleList;
import com.tang.admin.pojo.SaleListGoods;
import com.tang.admin.pojo.model.CountResultModel;
import com.tang.admin.pojo.model.SaleCount;
import com.tang.admin.query.SaleListQuery;
import com.tang.admin.service.*;
import com.tang.admin.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Resource
    private IGoodsTypeService goodsTypeService;



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
     *
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteSaleList(Integer id) {
        AssertUtil.isTrue(!(saleListGoodsService.remove(new QueryWrapper<SaleListGoods>().eq("sale_list_id", id))), "记录删除失败");
        AssertUtil.isTrue(!(this.removeById(id)), "记录删除失败");
    }

    /**
     * 结算销售订单
     *
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSaleListState(Integer id) {
        SaleList saleList = this.getById(id);
        AssertUtil.isTrue(null == saleList, "待结算的销售单不存在");
        AssertUtil.isTrue(saleList.getState() == 1, "该销售单已经结算");
        saleList.setState(1);
        AssertUtil.isTrue(!this.updateById(saleList), "销售单结算失败");
    }

    /**
     * 销售情况统计（综合订单表，订单商品表，商品表等进行多表全面的展示）
     * @param saleListQuery
     * @return
     */
    @Override
    public Map<String, Object> countSale(SaleListQuery saleListQuery) {
        if(null!=saleListQuery.getTypeId()){
            List<Integer> typeIds = goodsTypeService.queryAllSubTypeIdsByTypeId(saleListQuery.getTypeId());
            saleListQuery.setTypeIds(typeIds);
        }
        saleListQuery.setIndex((saleListQuery.getPage()-1)*saleListQuery.getLimit());
        Long count = this.baseMapper.countSaleTotal(saleListQuery);
        List<CountResultModel> list = this.baseMapper.saleListQueryList(saleListQuery);
        return PageResultUtil.getResult(count,list);
    }

    /**
     * 以日为单位 分组统计销售情况
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Map<String, Object> countDaySale(String begin, String end) {
        if (begin == null || end == null) {
            return PageResultUtil.getResult(0L, null);
        }
        // 对 已存在数据 以日为单位进行分组统计，得到的结果
        List<Map<String,Object>> list = this.baseMapper.countDaySale(begin,end);
        // 对这个结果进行进一步的包装。
        //      - 希望查询范围中，没有订单记录的日期也能展示数据
        //      - 金额数据的格式化
        // 手动构建每一天对应的展示数据，比对查询统计结果的日期，如果相同，金额数据 直接使用查询数据，如果不同，设为0
        // 存放结果记录
        List<SaleCount> saleCounts =new ArrayList<SaleCount>();
        // 获取每一天
        List<String> dates = DateUtil.getRangeDates(begin,end);
        for (String date : dates) {
            SaleCount saleCount =new SaleCount();
            saleCount.setDate(date);
            boolean flag =true;
            for(Map<String,Object> map:list){
                // 比对外循环当前天和统计结果中天是否一致
                String dd = map.get("saleDate").toString().substring(0,10);
                if(date.equals(dd)){
                    saleCount.setAmountCost(MathUtil.format2Bit(Float.parseFloat(map.get("amountCost").toString())));
                    saleCount.setAmountSale(MathUtil.format2Bit(Float.parseFloat(map.get("amountSale").toString())));
                    saleCount.setAmountProfit(MathUtil.format2Bit(saleCount.getAmountSale()-saleCount.getAmountCost()));
                    flag =false;
                }
            }
            if(flag){
                saleCount.setAmountProfit(0F);
                saleCount.setAmountSale(0F);
                saleCount.setAmountCost(0F);
            }
            saleCounts.add(saleCount);
        }
        return PageResultUtil.getResult((long) saleCounts.size(), saleCounts);
    }

    /**
     * 以月为单位 分组统计销售情况
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Map<String, Object> countMonthSale(String begin, String end) {
        if (begin == null || end == null) {
            return PageResultUtil.getResult(0L, null);
        }
        // 对 已存在数据 以日为单位进行分组统计，得到的结果
        List<Map<String,Object>> list = this.baseMapper.countMonthSale(begin,end);
        // 存放结果记录
        List<SaleCount> saleCounts =new ArrayList<>();
        // 获取每一月
        List<String> dates = DateUtil.getRangeMonth(begin,end);
        for (String date : dates) {
            SaleCount saleCount =new SaleCount();
            saleCount.setDate(date);
            boolean flag =true;
            for(Map<String,Object> map:list){
                // 比对外循环当前月和统计结果中月是否一致
                String dd = map.get("saleDate").toString().substring(0,7);
                if(date.equals(dd)){
                    saleCount.setAmountCost(MathUtil.format2Bit(Float.parseFloat(map.get("amountCost").toString())));
                    saleCount.setAmountSale(MathUtil.format2Bit(Float.parseFloat(map.get("amountSale").toString())));
                    saleCount.setAmountProfit(MathUtil.format2Bit(saleCount.getAmountSale()-saleCount.getAmountCost()));
                    flag =false;
                }
            }
            if(flag){
                saleCount.setAmountProfit(0F);
                saleCount.setAmountSale(0F);
                saleCount.setAmountCost(0F);
            }
            saleCounts.add(saleCount);
        }
        return PageResultUtil.getResult((long) saleCounts.size(), saleCounts);
    }



}
