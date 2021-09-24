package com.tang.admin.controller;


import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.Customer;
import com.tang.admin.query.CustomerQuery;
import com.tang.admin.service.ICustomerService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 客户表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Resource
    private ICustomerService customerService;

    @RequestMapping("/index")
    public String index() {
        return "/customer/customer";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> customersList(CustomerQuery customerQuery) {
        return customerService.customerList(customerQuery);
    }

    @RequestMapping("/addOrUpdateCustomerPage")
    public String addOrUpdateCustomerPage(Integer id, Model model) {
        if (id != null) {
            model.addAttribute("customer", customerService.getById(id));
        }
        return "/customer/add_update";
    }

    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Customer customer) {
        customerService.saveCustomer(customer);
        return RespBean.success("客户添加成功");
    }

    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(Customer customer) {
        customerService.updateCustomer(customer);
        return RespBean.success("客户更新成功");
    }

    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer[] ids) {
        customerService.deleteCustomer(ids);
        return RespBean.success("客户删除成功");
    }
}