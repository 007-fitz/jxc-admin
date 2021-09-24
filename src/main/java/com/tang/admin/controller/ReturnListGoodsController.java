package com.tang.admin.controller;


import com.tang.admin.query.ReturnListGoodsQuery;
import com.tang.admin.service.IReturnListGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 退货单商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/returnListGoods")
public class ReturnListGoodsController {

    @Resource
    private IReturnListGoodsService returnListGoodsService;

    /**
     * 出库订单中包含的商品的展示
     * @param returnListGoodsQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(ReturnListGoodsQuery returnListGoodsQuery) {
        return returnListGoodsService.listReturnListGoods(returnListGoodsQuery);
    }

}
