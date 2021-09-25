package com.tang.admin.controller;


import com.tang.admin.query.DamageListGoodsQuery;
import com.tang.admin.service.IDamageListGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 报损单商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Controller
@RequestMapping("/damageListGoods")
public class DamageListGoodsController {

    @Resource
    private IDamageListGoodsService damageListGoodsService;

    /**
     * 报损单中商品信息的展示
     * @param damageListGoodsQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(DamageListGoodsQuery damageListGoodsQuery) {
        return damageListGoodsService.listDamageListGoods(damageListGoodsQuery);
    }

}
