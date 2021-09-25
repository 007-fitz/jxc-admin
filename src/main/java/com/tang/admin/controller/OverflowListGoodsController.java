package com.tang.admin.controller;


import com.tang.admin.query.OverflowListGoodsQuery;
import com.tang.admin.service.IOverflowListGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 报溢单商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Controller
@RequestMapping("/overflowListGoods")
public class OverflowListGoodsController {

    @Resource
    private IOverflowListGoodsService overflowListGoodsService;

    /**
     * 报溢单中包含的商品信息展示
     * @param overflowListGoodsQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(OverflowListGoodsQuery overflowListGoodsQuery) {
        return overflowListGoodsService.listOverflowListGoods(overflowListGoodsQuery);
    }


}
