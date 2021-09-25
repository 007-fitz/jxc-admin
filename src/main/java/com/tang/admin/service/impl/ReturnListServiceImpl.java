package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.ReturnList;
import com.tang.admin.mapper.ReturnListMapper;
import com.tang.admin.pojo.ReturnListGoods;
import com.tang.admin.query.ReturnListQuery;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.IReturnListGoodsService;
import com.tang.admin.service.IReturnListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * 退货单表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Service
public class ReturnListServiceImpl extends ServiceImpl<ReturnListMapper, ReturnList> implements IReturnListService {

    @Resource
    private IUserService userService;

    @Resource
    private IGoodsService goodsService;

    @Resource
    private IReturnListGoodsService returnListGoodsService;

    /**
     * 生成出库编号
     * @return
     */
    @Override
    public String getNextReturnNumber() {
        //tH20218070001X
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("TH");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String returnNumber = this.baseMapper.getNextReturnNumber(stringBuffer.toString());
            if (null != returnNumber) {
                stringBuffer.append(StringUtil.formatCode(returnNumber));
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
     * 创建出库订单
     * @param username 当前用户用户名
     * @param returnList 出库订单
     * @param rlgList 出库订单中包含的商品
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveReturnList(String username, ReturnList returnList, List<ReturnListGoods> rlgList) {
        int count = this.count(new QueryWrapper<ReturnList>().eq("return_number", returnList.getReturnNumber()));
        AssertUtil.isTrue(count != 0, "订单编号已存在");
        AssertUtil.isTrue(null == returnList.getSupplierId(), "请指定供应商");
        AssertUtil.isTrue(null == rlgList, "请选择商品");

        returnList.setUserId(userService.findByUsername(username).getId());
        AssertUtil.isTrue(!this.save(returnList), "出库单据创建失败01");

        ReturnList temp = this.getOne(new QueryWrapper<ReturnList>().eq("return_number", returnList.getReturnNumber()));
        rlgList.forEach(rlg -> {
            rlg.setReturnListId(temp.getId());
            // 减库存
            Goods goods = goodsService.getById(rlg.getGoodsId());
            int newInventory = goods.getInventoryQuantity() - rlg.getNum();
            AssertUtil.isTrue(newInventory < 0, "商品 <" + goods.getName() + "> 待出库数量超出库存总数");
            goods.setInventoryQuantity(newInventory);
            goods.setState(2);
            goodsService.updateById(goods);
        });
        AssertUtil.isTrue(!returnListGoodsService.saveBatch(rlgList), "出库单据创建失败02");
    }

    /**
     * 条件性 展示退货出库订单
     * @param returnListQuery
     * @return
     */
    @Override
    public Map<String, Object> listReturnList(ReturnListQuery returnListQuery) {
        IPage<ReturnList> page = new Page<ReturnList>(returnListQuery.getPage(), returnListQuery.getLimit());
        page = this.baseMapper.listReturnList(page, returnListQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 删除退货出库订单，同时删除与其关联的商品信息
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteReturnListService(Integer id) {
        AssertUtil.isTrue(!returnListGoodsService.remove(new QueryWrapper<ReturnListGoods>().eq("return_list_id", id)), "退货出库订单删除失败01");
        AssertUtil.isTrue(!this.removeById(id), "退货出库订单删除失败02");
    }


}
