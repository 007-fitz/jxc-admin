package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.Goods;
import com.tang.admin.mapper.GoodsMapper;
import com.tang.admin.query.GoodsQuery;
import com.tang.admin.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.service.IGoodsTypeService;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.PageResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 服务实现类
 *    - 不同于商城项目中的商品表(特定商家/供应商关联特定商品，用户从众多商品中，选择加入购物车，购物车中多条记录合并为一个订单)，
 *    - 此处的商品表偏向于公司自身的需求，记录进表中的都是公司需求的。
 *      故商品和库存1:1合并，共用一张表。商品自身信息的维护、商品库存的维护 两个方面的操作从逻辑上大致分开。
 *      面向供应商的采购/退货体系，面向客户的销售/退货体系 两方面，最终主要的影响都是影响库存
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Resource
    private IGoodsTypeService goodsTypeService;

    /**
     * 生成4为编码
     *
     * @return
     */
    @Override
    public String genGoodsCode() {
        String maxGoodsCode = this.baseMapper.selectOne(new QueryWrapper<Goods>().select("max(code) as code")).getCode();
        if (StringUtils.isNotEmpty(maxGoodsCode)) {
            Integer code = Integer.valueOf(maxGoodsCode) + 1;
            String codes = code.toString();
            int length = codes.length();
            for (int i = 4; i > length; i--) {
                codes = "0" + codes;
            }
            return codes;
        } else {
            return "0001";
        }
    }

    /**
     * 选择性展示商品信息
     * @param goodsQuery
     * @return
     */
    @Override
    public Map<String, Object> goodsList(GoodsQuery goodsQuery) {
        IPage<Goods> page = new Page<Goods>(goodsQuery.getPage(), goodsQuery.getLimit());
        if (null != goodsQuery.getTypeId()) {
            goodsQuery.setTypeIds(goodsTypeService.queryAllSubTypeIdsByTypeId(goodsQuery.getTypeId()));
        }
        page = this.baseMapper.queryGoodsByParams(page, goodsQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 新增商品
     * @param goods
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveGoods(Goods goods) {
        /**
         * 1.参数校验
         *  商品名非空  类别非空  单位非空
         *  2.设置商品唯一编码，
         *  3.其他参数设置
         *      默认设置库存0，
         *      商品状态 state 0
         *      采购价格 0
         *      isdel 0
         */

        AssertUtil.isTrue(StringUtils.isBlank(goods.getName()), "请指定商品名称");
        AssertUtil.isTrue(null == goods.getTypeId(), "请指定商品类别");
        AssertUtil.isTrue(StringUtils.isBlank(goods.getUnit()), "请指定商品单位");
        goods.setCode(genGoodsCode());
        goods.setInventoryQuantity(0);
        goods.setState(0);
        goods.setLastPurchasingPrice(0F);
        goods.setIsDel(0);
        AssertUtil.isTrue(!(this.save(goods)), "商品记录添加失败!");
    }

    /**
     * 更新商品信息
     * @param goods
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateGoods(Goods goods) {
        /**
         * 1.参数校验
         *      商品名非空  类别非空  单位非空
         */
        AssertUtil.isTrue(StringUtils.isBlank(goods.getName()), "请指定商品名称");
        AssertUtil.isTrue(null == goods.getTypeId(), "请指定商品类别");
        AssertUtil.isTrue(StringUtils.isBlank(goods.getUnit()), "请指定商品单位");
        AssertUtil.isTrue(!(this.updateById(goods)), "商品更新失败");
    }

    /**
     * 删除商品信息
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteGoods(Integer id) {
        /**
         * 1.记录必须存在
         * 2.不可删除条件
         *    如果商品已经期初入库 不可删除
         *    商品已发生单据 不能删除
         * 3.执行更新   isDel 0->1
         */
        Goods goods = this.getById(id);
        AssertUtil.isTrue(null == goods, "待删除的商品记录不存在");
        AssertUtil.isTrue(goods.getState() == 1, "该商品已经期初入库，不能删除");
        AssertUtil.isTrue(goods.getState() == 2, "该商品已经单据，不能删除!");
        goods.setIsDel(1);
        AssertUtil.isTrue(!(this.updateById(goods)), "商品删除失败!");
    }

    /**
     * 更新指定商品库存
     * @param goods
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateStock(Goods goods) {
        /**
         * 商品库存量>0
         * 商品成本价>0
         */
        AssertUtil.isTrue(null == goods.getId() || null == this.getById(goods.getId()), "待更新的商品记录不存在");
        AssertUtil.isTrue(goods.getInventoryQuantity() <= 0, "库存量必须>0");
        AssertUtil.isTrue(goods.getPurchasingPrice() <= 0, "成本价必须大于0");
        AssertUtil.isTrue(!(this.updateById(goods)), "商品更新失败!");
    }

    /**
     * 删除指定商品库存
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteStock(Integer id) {
        Goods temp = this.getById(id);
        AssertUtil.isTrue(null == temp, "待更新的商品记录不存在!");
        AssertUtil.isTrue(temp.getState() == 2, "该商品已经发生单据，不可删除!");
        temp.setInventoryQuantity(0);
        AssertUtil.isTrue(!(this.updateById(temp)), "商品删除失败!");
    }

    /**
     * 获得指定商品信息
     * @param gid
     * @return
     */
    @Override
    public Goods getGoodsInfoById(Integer gid) {
        return this.baseMapper.getGoodsInfoById(gid);
    }

    /**
     * 商品库存展示
     * @param goodsQuery
     * @return
     */
    @Override
    public Map<String, Object> stockList(GoodsQuery goodsQuery) {
        IPage<Goods> page = new Page<Goods>(goodsQuery.getPage(),goodsQuery.getLimit());

        if(null !=goodsQuery.getTypeId()){
            goodsQuery.setTypeIds(goodsTypeService.queryAllSubTypeIdsByTypeId(goodsQuery.getTypeId()));
        }
        page =  this.baseMapper.queryGoodsByParams(page,goodsQuery);
        List<Goods> goodsList = page.getRecords();
//        goodsList.forEach(g->{
//            g.setSaleTotal( saleListGoodsService.getSaleTotalByGoodIs(g.getId()) -
//                    customerReturnListGoodsService.getReturnTotalByGoodId(g.getId()));
//        });
        return PageResultUtil.getResult(page.getTotal(),page.getRecords());
    }

//    @Resource
//    private ISaleListGoodsService saleListGoodsService;
//
//    @Resource
//    private ICustomerReturnListGoodsService customerReturnListGoodsService;

}
