package com.tang.admin.service;

import com.tang.admin.pojo.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.SupplierQuery;

import java.util.Map;

/**
 * <p>
 * 供应商表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
public interface ISupplierService extends IService<Supplier> {

    Supplier findSupplierByName(String name);

    Map<String, Object> listSupplier(SupplierQuery supplierQuery);

    void saveSupplier(Supplier supplier);

    void updateSupplier(Supplier supplier);

    void deleteSupplier(Integer[] ids);

}
