package com.tang.admin.service;

import com.tang.admin.pojo.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.admin.query.CustomerQuery;

import java.util.Map;

/**
 * <p>
 * 客户表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
public interface ICustomerService extends IService<Customer> {

    Map<String, Object> customerList(CustomerQuery customerQuery);

    /**
     * 新增客户信息
     * @param customer
     */
    void saveCustomer(Customer customer);

    Customer findCustomerByName(String name);

    void updateCustomer(Customer customer);

    void deleteCustomer(Integer[] ids);

}
