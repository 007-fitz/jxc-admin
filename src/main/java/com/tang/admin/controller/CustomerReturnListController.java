package com.tang.admin.controller;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tang.admin.pojo.CustomerReturnList;
import com.tang.admin.pojo.CustomerReturnListGoods;
import com.tang.admin.pojo.SaleListGoods;
import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.query.CustomerReturnListQuery;
import com.tang.admin.service.ICustomerReturnListService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户退货单表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Controller
@RequestMapping("/customerReturn")
public class CustomerReturnListController {

    @Resource
    private ICustomerReturnListService customerReturnListService;

    /**
     * 客户退货订单创建页面
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("customerReturnNumber", customerReturnListService.getNextCustomerReturnNumber());
        return "/customerReturn/customer_return";
    }

    /**
     * 客户退货订单创建
     * @param principal
     * @param customerReturnList
     * @param goodsJson
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Principal principal, CustomerReturnList customerReturnList, String goodsJson) {
        Gson gson = new Gson();
        List<CustomerReturnListGoods> crlgList = gson.fromJson(goodsJson, new TypeToken<List<CustomerReturnListGoods>>() {
        }.getType());
        customerReturnListService.saveCustomerReturnList(principal.getName(), customerReturnList, crlgList);
        return RespBean.success("客户退货订单创建成功");
    }

    /**
     * 客户退货订单查询页面
     * @return
     */
    @RequestMapping("/searchPage")
    public String searchPage() {
        return "/customerReturn/customer_return_search";
    }

    /**
     * 条件性查询客户退货订单信息
     * @param customerReturnListQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(CustomerReturnListQuery customerReturnListQuery) {
        return customerReturnListService.listCustomerReturnList(customerReturnListQuery);
    }

    /**
     * 删除客户退货订单，及其关联包含的商品信息
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        customerReturnListService.deleteSaleList(id);
        return RespBean.success("客户退货订单删除成功");
    }

}
