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

}
