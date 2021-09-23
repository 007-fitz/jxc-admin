package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.Supplier;
import com.tang.admin.mapper.SupplierMapper;
import com.tang.admin.query.SupplierQuery;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.ISupplierService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.PageResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 供应商表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    @Resource
    private IGoodsService goodsService;

    @Override
    public Supplier findSupplierByName(String name) {
        return this.getOne(new QueryWrapper<Supplier>().eq("is_del", 0).eq("name", name));
    }

    @Override
    public Map<String, Object> listSupplier(SupplierQuery supplierQuery) {
        IPage<Supplier> page = new Page<Supplier>(supplierQuery.getPage(), supplierQuery.getLimit());
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", 0);
        if (StringUtils.isNotBlank(supplierQuery.getSupplierName())) {
            queryWrapper.like("name", supplierQuery.getSupplierName());
        }
        page = this.baseMapper.selectPage(page, queryWrapper);
        return PageResultUtil.getResult(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveSupplier(Supplier supplier) {
        /**
         * 供应商名称  联系人 联系电话   非空
         * 名称不可重复
         * isDel 0
         */
        checkParams(supplier.getName(), supplier.getContact(), supplier.getNumber());
        AssertUtil.isTrue(null != this.findSupplierByName(supplier.getName()), "供应商已存在!");
        supplier.setIsDel(0);
        AssertUtil.isTrue(!(this.save(supplier)), "记录添加失败!");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void updateSupplier(Supplier supplier) {
        AssertUtil.isTrue(null == supplier.getId() || null == this.getById(supplier.getId()), "请选择供应商记录!");
        checkParams(supplier.getName(), supplier.getContact(), supplier.getNumber());
        Supplier temp = this.findSupplierByName(supplier.getName());
        AssertUtil.isTrue(null != temp && !temp.getId().equals(supplier.getId()), "供应商已存在");
        AssertUtil.isTrue(!(this.updateById(supplier)), "记录更新失败!");
    }

    private void checkParams(String name, String contact, String number) {
        AssertUtil.isTrue(StringUtils.isBlank(name), "请输入供应商名称!");
        AssertUtil.isTrue(StringUtils.isBlank(contact), "请输入联系人!");
        AssertUtil.isTrue(StringUtils.isBlank(number), "请输入联系电话!");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void deleteSupplier(Integer[] ids) {
        AssertUtil.isTrue(null == ids || ids.length == 0, "请选择待删除记录");
        List<Supplier> suppliers = this.baseMapper.selectBatchIds(Arrays.asList(ids));
        // 获取name集合，外键约束
//        List<String> supplierNames = suppliers.stream().map(Supplier::getName).collect(Collectors.toList());
//        int relationCount = goodsService.count(new QueryWrapper<Goods>().in("producer", supplierNames));
//        AssertUtil.isTrue(relationCount>0, "当前供应商还存在商品记录，暂不提供删除操作");
        // 遍历设置删除位为1
        suppliers.forEach(supplier -> {
            supplier.setIsDel(1);
        });
        AssertUtil.isTrue(!this.updateBatchById(suppliers), "记录删除失败");

        /*
        List<Supplier> suppliers = new ArrayList<>();
        for (Integer id : ids) {
            Supplier supplier = new Supplier();
            supplier.setId(id);
            supplier.setIsDel(1);
            suppliers.add(supplier);
        }
        AssertUtil.isTrue(!this.updateBatchById(suppliers), "记录删除失败");
        */
        /*
        根据ids，进行批量删除(实际为更新删除位)。
            两种方式。
                - 遍历ids，为每个id手动创建bean对象，设置删除位为1
                - 根据ids查出所有记录，遍历所有记录，将删除位设置位1
            将得到的List，拿去更新操作。

            第二种方式，由于获取了所有信息，所以再次更新时，其他字段会重复更新(没必要)，故可能所需代价稍高一些
            此处使用第二种，是因为需要获得name的List。从而能够去手动定义外键删除规则
         */
    }
}
