package com.tang.admin.controller;


import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.GoodsType;
import com.tang.admin.query.GoodsQuery;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.IGoodsTypeService;
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

    /**
     * 商品展示 页面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "/goods/goods";
    }

    /**
     * 商品展示
     * @param goodsQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> goodsList(GoodsQuery goodsQuery) {
        return goodsService.goodsList(goodsQuery);
    }

    /**
     * 商品新增/更新 页面
     * @param id
     * @param typeId
     * @param model
     * @return
     */
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

    /**
     * 商品类别展示页面，以提供选择 商品和商品类别 之间的关系
     * @param typeId
     * @param model
     * @return
     */
    @RequestMapping("/toGoodsTypePage")
    public String toGoodsTypePage(Integer typeId, Model model) {
        model.addAttribute("typeId", typeId);
        return "/goods/goods_type";
    }

    /**
     * 新增商品信息
     * @param goods
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Goods goods) {
        goodsService.saveGoods(goods);
        return RespBean.success("商品添加成功");
    }

    /**
     * 更新商品信息
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(Goods goods) {
        goodsService.updateGoods(goods);
        return RespBean.success("商品更新成功");
    }

    /**
     * 删除商品记录
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        goodsService.deleteGoods(id);
        return RespBean.success("商品删除成功");
    }

}
