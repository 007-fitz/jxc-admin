package com.tang.admin.controller;


import com.tang.admin.query.CustomerReturnListGoodsQuery;
import com.tang.admin.service.ICustomerReturnListGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 客户退货单商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Controller
@RequestMapping("/customerReturnListGoods")
public class CustomerReturnListGoodsController {

    @Resource
    private ICustomerReturnListGoodsService customerReturnListGoodsService;

    /**
     * 查询客户退货订单中包含的商品信息
     * @param customerReturnListGoodsQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(CustomerReturnListGoodsQuery customerReturnListGoodsQuery) {
        return customerReturnListGoodsService.listCustomerReturnListGoods(customerReturnListGoodsQuery);
    }

}
