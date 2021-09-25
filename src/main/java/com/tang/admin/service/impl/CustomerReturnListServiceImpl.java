package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.CustomerReturnList;
import com.tang.admin.mapper.CustomerReturnListMapper;
import com.tang.admin.pojo.CustomerReturnListGoods;
import com.tang.admin.pojo.Goods;
import com.tang.admin.query.CustomerReturnListQuery;
import com.tang.admin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * 客户退货单表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Service
public class CustomerReturnListServiceImpl extends ServiceImpl<CustomerReturnListMapper, CustomerReturnList> implements ICustomerReturnListService {

    @Resource
    private IUserService userService;

    @Resource
    private ICustomerReturnListGoodsService customerReturnListGoodsService;

    @Resource
    private IGoodsService goodsService;

    /**
     * 生成客户退货订单编号
     * @return
     */
    @Override
    public String getNextCustomerReturnNumber() {
        try {
            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append("XT");
            stringBuffer.append(DateUtil.getCurrentDateStr());
            String customerReturnNumber = this.baseMapper.getNextCustomerReturnNumber(stringBuffer.toString());
            if(null !=customerReturnNumber){
                stringBuffer.append(StringUtil.formatCode(customerReturnNumber));
            }else{
                stringBuffer.append("0001");
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 创建客户退货订单
     * @param username
     * @param customerReturnList
     * @param crlgList
     */
    @Override
    public void saveCustomerReturnList(String username, CustomerReturnList customerReturnList, List<CustomerReturnListGoods> crlgList) {
        int count = this.count(new QueryWrapper<CustomerReturnList>().eq("customer_return_number", customerReturnList.getCustomerReturnNumber()));
        AssertUtil.isTrue(count != 0, "订单编号已存在");
        AssertUtil.isTrue(null==customerReturnList.getCustomerId() || customerReturnList.getCustomerId()==0, "请选择客户");
        AssertUtil.isTrue(null == customerReturnList.getCustomerReturnDate(), "请选择日期");
        AssertUtil.isTrue(null == crlgList, "请选择商品");

        customerReturnList.setUserId(userService.findByUsername(username).getId());
        AssertUtil.isTrue(!this.save(customerReturnList), "客户退货订单创建失败01");

        CustomerReturnList temp = this.getOne(new QueryWrapper<CustomerReturnList>().eq("customer_return_number", customerReturnList.getCustomerReturnNumber()));
        crlgList.forEach(crlg -> {
            crlg.setCustomerReturnListId(temp.getId());
            // 更新库存
            Goods goods = goodsService.getById(crlg.getGoodsId());
            goods.setInventoryQuantity(goods.getInventoryQuantity() + crlg.getNum());
            goods.setState(2);
            AssertUtil.isTrue(!goodsService.updateById(goods), "库存更新出现错误");
        });
        AssertUtil.isTrue(!customerReturnListGoodsService.saveBatch(crlgList), "客户退货订单创建失败02");
    }

    /**
     * 条件性查询客户退货订单信息
     * @param customerReturnListQuery
     * @return
     */
    @Override
    public Map<String, Object> listCustomerReturnList(CustomerReturnListQuery customerReturnListQuery) {
        IPage<CustomerReturnList> page = new Page<>(customerReturnListQuery.getPage(), customerReturnListQuery.getLimit());
        page = this.baseMapper.listCustomerReturnList(page, customerReturnListQuery);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    /**
     * 删除客户退货订单，及其关联包含的商品信息
     * @param id
     */
    @Override
    public void deleteSaleList(Integer id) {
        AssertUtil.isTrue(!(customerReturnListGoodsService.remove(new QueryWrapper<CustomerReturnListGoods>().eq("customer_return_list_id",id))),"记录删除失败");
        AssertUtil.isTrue(!(this.removeById(id)),"记录删除失败");
    }


}
