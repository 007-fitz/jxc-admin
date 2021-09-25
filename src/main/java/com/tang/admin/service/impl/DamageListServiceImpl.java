package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.DamageList;
import com.tang.admin.mapper.DamageListMapper;
import com.tang.admin.pojo.DamageListGoods;
import com.tang.admin.pojo.Goods;
import com.tang.admin.query.DamageListQuery;
import com.tang.admin.service.IDamageListGoodsService;
import com.tang.admin.service.IDamageListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.service.IGoodsService;
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
 * 报损单表 服务实现类
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
public class DamageListServiceImpl extends ServiceImpl<DamageListMapper, DamageList> implements IDamageListService {

    @Resource
    private IUserService userService;

    @Resource
    private IDamageListGoodsService damageListGoodsService;

    @Resource
    private IGoodsService goodsService;

    /**
     * 生成报损单编号
     * @return
     */
    @Override
    public String getNextDamageNumber() {
        try {
            StringBuffer  stringBuffer = new StringBuffer();
            stringBuffer.append("BS");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String saleNumber = this.baseMapper.getNextDamageNumber(stringBuffer.toString());
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
     * 新增报损单记录，同时保存其中包含的商品报损信息
     * @param username
     * @param damageList
     * @param dlgList
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveDamageList(String username, DamageList damageList, List<DamageListGoods> dlgList) {
        int count = this.count(new QueryWrapper<DamageList>().eq("damage_number", damageList.getDamageNumber()));
        AssertUtil.isTrue(count != 0, "订单编号已存在");
        AssertUtil.isTrue(null == damageList.getDamageDate(), "请选择日期");
        AssertUtil.isTrue(null == dlgList, "请选择商品");

        damageList.setUserId(userService.findByUsername(username).getId());
        AssertUtil.isTrue(!this.save(damageList), "商品报损记录创建失败01");

        DamageList temp = this.getOne(new QueryWrapper<DamageList>().eq("damage_number", damageList.getDamageNumber()));
        dlgList.forEach(dlg -> {
            dlg.setDamageListId(temp.getId());
            // 对库存进行校正，降低到实际量
            Goods goods = goodsService.getById(dlg.getGoodsId());
            int newInventory = goods.getInventoryQuantity() - dlg.getNum();
            AssertUtil.isTrue(newInventory < 0, "商品 <" +goods.getName()+ "> 报损校正量超出范围");
            goods.setInventoryQuantity(newInventory);
            AssertUtil.isTrue(!goodsService.updateById(goods), "商品库存更新失败");
        });
        AssertUtil.isTrue(!damageListGoodsService.saveBatch(dlgList), "商品报损记录创建失败02");
    }

    /**
     * 条件性展示报损单
     * @param damageListQuery
     * @return
     */
    @Override
    public Map<String, Object> listDamageList(DamageListQuery damageListQuery) {
        IPage<DamageList> page = new Page<>(damageListQuery.getPage(), damageListQuery.getLimit());
        page = this.baseMapper.listDamageList(page, damageListQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 删除报损单，同时删除报损单中包含的商品信息
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteDamageList(Integer id) {
        AssertUtil.isTrue(!damageListGoodsService.remove(new QueryWrapper<DamageListGoods>().eq("damage_list_id", id)), "单据关联的商品信息删除失败");
        AssertUtil.isTrue(!this.removeById(id), "单据删除失败");
    }


}
