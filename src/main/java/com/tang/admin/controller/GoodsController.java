package com.tang.admin.controller;


import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.GoodsType;
import com.tang.admin.query.GoodsQuery;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.IGoodsTypeService;
import com.tang.admin.service.ISupplierService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private IGoodsService goodsService;

    @Resource
    private IGoodsTypeService goodsTypeService;

    @RequestMapping("/index")
    public String index() {
        return "/goods/goods";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> goodsList(GoodsQuery goodsQuery) {
        return goodsService.goodsList(goodsQuery);
    }

    @RequestMapping("/addOrUpdateGoodsPage")
    public String addOrUpdateGoodsPage(Integer id, Integer typeId, Model model) {
        if (id != null) {
            Goods goods = goodsService.getById(id);
            GoodsType goodsType = goodsTypeService.getById(goods.getTypeId());
            model.addAttribute("goods", goods);
            model.addAttribute("goodsType", goodsType);
        } else if (typeId != null) {
            GoodsType goodsType = goodsTypeService.getById(typeId);
            model.addAttribute("goodsType", goodsType);
        }
        return "/goods/add_update";
    }

    @RequestMapping("/toGoodsTypePage")
    public String toGoodsTypePage(Integer typeId, Model model) {
        model.addAttribute("typeId", typeId);
        return "/goods/goods_type";
    }

    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Goods goods) {
        goodsService.saveGoods(goods);
        return RespBean.success("商品添加成功");
    }

    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(Goods goods) {
        goodsService.updateGoods(goods);
        return RespBean.success("商品更新成功");
    }

    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        goodsService.deleteGoods(id);
        return RespBean.success("商品删除成功");
    }

}
