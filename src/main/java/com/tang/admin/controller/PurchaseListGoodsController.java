package com.tang.admin.controller;


import com.tang.admin.query.PurchaseListGoodsQuery;
import com.tang.admin.service.IPurchaseListGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 进货单商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/purchaseListGoods")
public class PurchaseListGoodsController {

    @Resource
    private IPurchaseListGoodsService purchaseListGoodsService;

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> listPurchaseListGoods(PurchaseListGoodsQuery purchaseListGoodsQuery) {
        return purchaseListGoodsService.listPurchaseListGoods(purchaseListGoodsQuery);
    }


}
