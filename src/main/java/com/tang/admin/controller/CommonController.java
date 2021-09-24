package com.tang.admin.controller;

import com.tang.admin.service.IGoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/common")
public class CommonController {
    @Resource
    private IGoodsService goodsService;

    @RequestMapping("/toSelectGoodsPage")
    public String toSelectGoodsPage() {
        return "/common/goods";
    }

    @RequestMapping("/toAddGoodsInfoPage")
    public String toAddGoodsInfoPage(Integer gid, Model model) {
        if (gid != null) {
            model.addAttribute("goods", goodsService.getGoodsInfoById(gid));
        }
        return "/common/goods_add_update";
    }

    @RequestMapping("/toUpdateGoodsInfoPage")
    public String toUpdateGoodsInfoPage(Integer id, String price, Integer num, Model model) {
        if (id != null) {
            model.addAttribute("goods", goodsService.getGoodsInfoById(id));
        }
        if (price != null) {
            model.addAttribute("price", price);
        }
        if (num != null) {
            model.addAttribute("num", num);
        }
        return "/common/goods_add_update";
    }
}

