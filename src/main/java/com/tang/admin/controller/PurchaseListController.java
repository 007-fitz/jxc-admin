package com.tang.admin.controller;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.pojo.PurchaseList;
import com.tang.admin.pojo.PurchaseListGoods;
import com.tang.admin.query.PurchaseListQuery;
import com.tang.admin.service.IPurchaseListService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 进货单 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/purchase")
public class PurchaseListController {

    @Resource
    private IPurchaseListService purchaseListService;

    /**
     * 订单创建页面
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model) {
        //获取进货单号
        String purchaseNumber = purchaseListService.getNextPurchaseNumber();
        model.addAttribute("purchaseNumber",purchaseNumber);
        return "/purchase/purchase";
    }

    /**
     * 订单创建
     * @param principal
     * @param purchaseList
     * @param goodsJson
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Principal principal, PurchaseList purchaseList, String goodsJson) {
        Gson gson = new Gson();
        List<PurchaseListGoods> plgList =gson.fromJson(goodsJson,new TypeToken<List<PurchaseListGoods>>(){}.getType());
        purchaseListService.savePurchaseList(principal.getName(), purchaseList, plgList);
        return RespBean.success("商品进货入库成功");
    }

    /**
     * 订单查询页面
     * @return
     */
    @RequestMapping("/searchPage")
    public String searchPage() {
        return "/purchase/purchase_search";
    }

    /**
     * 订单查询
     * @param purchaseListQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(PurchaseListQuery purchaseListQuery) {
        return purchaseListService.listPurchaseList(purchaseListQuery);
    }


}
