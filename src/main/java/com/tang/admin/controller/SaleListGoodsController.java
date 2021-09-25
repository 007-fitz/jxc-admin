package com.tang.admin.controller;


import com.tang.admin.query.SaleListGoodsQuery;
import com.tang.admin.service.ISaleListGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 销售单商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Controller
@RequestMapping("/saleListGoods")
public class SaleListGoodsController {

    @Resource
    private ISaleListGoodsService saleListGoodsService;

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(SaleListGoodsQuery saleListGoodsQuery) {
        return saleListGoodsService.listSaleListGoods(saleListGoodsQuery);
    }



}
