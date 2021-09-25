package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.DamageList;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.OverflowList;
import com.tang.admin.mapper.OverflowListMapper;
import com.tang.admin.pojo.OverflowListGoods;
import com.tang.admin.query.OverFlowListQuery;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.IOverflowListGoodsService;
import com.tang.admin.service.IOverflowListService;
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
 * 报溢单表 服务实现类
 *
 * 商品报损，系统数据中虚高 现实中商品数量更低。故进行校正（减库存）
 *      ------> 可能是 对项目系统的人工操作/录入有误，导致数据错误，所以进行更正。
 *      ------> 可能是 受损、过期商品等进行低价处理，从而产生的商品报损
 * 商品报溢，系统数据中偏低 现实中商品数量更高。故进行校正（库存加）
 *      ------> 可能是对项目系统的操作有误，导致数据错误，所以进行更正。
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Service
public class OverflowListServiceImpl extends ServiceImpl<OverflowListMapper, OverflowList> implements IOverflowListService {

    @Resource
    private IUserService userService;

    @Resource
    private IOverflowListGoodsService overflowListGoodsService;

    @Resource
    private IGoodsService goodsService;

    /**
     * 生成商品报溢单编号
     * @return
     */
    @Override
    public String getOverflowNumber() {
        try {
            StringBuffer  stringBuffer = new StringBuffer();
            stringBuffer.append("BY");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String saleNumber = this.baseMapper.getOverflowNumber(stringBuffer.toString());
            if(null!=saleNumber){
                stringBuffer.append(StringUtil.formatCode(saleNumber));
            }else {
                stringBuffer.append("0001");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 新增商品报溢单，同时保存其中包含的商品信息
     * @param username
     * @param overflowList
     * @param olgList
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveOverflowList(String username, OverflowList overflowList, List<OverflowListGoods> olgList) {
        int count = this.count(new QueryWrapper<OverflowList>().eq("overflow_number", overflowList.getOverflowNumber()));
        AssertUtil.isTrue(count != 0, "订单编号已存在");
        AssertUtil.isTrue(null == overflowList.getOverflowDate(), "请选择日期");
        AssertUtil.isTrue(null == olgList, "请选择商品");

        overflowList.setUserId(userService.findByUsername(username).getId());
        AssertUtil.isTrue(!this.save(overflowList), "商品报溢记录单创建失败01");

        OverflowList temp = this.getOne(new QueryWrapper<OverflowList>().eq("overflow_number", overflowList.getOverflowNumber()));
        olgList.forEach(olg -> {
            olg.setOverflowListId(temp.getId());
            // 对库存进行校正，增加到实际量
            Goods goods = goodsService.getById(olg.getGoodsId());
            goods.setInventoryQuantity(goods.getInventoryQuantity() + olg.getNum());
            AssertUtil.isTrue(!goodsService.updateById(goods), "商品库存更新失败");
        });
        AssertUtil.isTrue(!overflowListGoodsService.saveBatch(olgList), "商品报溢记录单创建失败02");
    }

    /**
     * 报溢单条件性展示
     * @param overFlowListQuery
     * @return
     */
    @Override
    public Map<String, Object> listOverFlowList(OverFlowListQuery overFlowListQuery) {
        IPage<OverflowList> page = new Page<>(overFlowListQuery.getPage(), overFlowListQuery.getLimit());
        page = this.baseMapper.listOverFlowList(page, overFlowListQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 删除报溢单，及其包含的商品信息
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteOverflowList(Integer id) {
        AssertUtil.isTrue(!overflowListGoodsService.remove(new QueryWrapper<OverflowListGoods>().eq("overflow_list_id", id)), "报溢单关联的商品记录删除失败");
        AssertUtil.isTrue(!this.removeById(id), "报溢单删除失败");
    }

}
