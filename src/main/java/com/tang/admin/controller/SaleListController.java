package com.tang.admin.controller;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tang.admin.pojo.SaleList;
import com.tang.admin.pojo.SaleListGoods;
import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.query.SaleListQuery;
import com.tang.admin.service.ISaleListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 销售单表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
@Controller
@RequestMapping("/sale")
public class SaleListController {

    @Resource
    private ISaleListService saleListService;

    /**
     * 销售订单创建 页面
     *
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("saleNumber", saleListService.getNextSaleNumber());
        return "/sale/sale";
    }

    /**
     * 创建销售订单
     *
     * @param principal
     * @param saleList
     * @param goodsJson
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Principal principal, SaleList saleList, String goodsJson) {
        Gson gson = new Gson();
        List<SaleListGoods> slgList = gson.fromJson(goodsJson, new TypeToken<List<SaleListGoods>>() {}.getType());
        saleListService.saveSaleList(principal.getName(), saleList, slgList);
        return RespBean.success("销售订单创建成功");
    }

    /**
     * 销售订单信息查看页面
     *
     * @return
     */
    @RequestMapping("/searchPage")
    public String searchPage() {
        return "/sale/sale_search";
    }

    /**
     * 销售订单查询
     * @param saleListQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(SaleListQuery saleListQuery) {
        return saleListService.listSaleList(saleListQuery);
    }

    /**
     * 删除销售订单，及其关联包含的商品信息
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        saleListService.deleteSaleList(id);
        return RespBean.success("销售订单删除成功");
    }

}
