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
 * (某用户从某供应商进货，创建进货入库订单，一个订单包含多个商品信息，1:1使对应商品库存增加)
 * </p>
 *
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

    /**
     * 删除进货订单
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        purchaseListService.deletePurchaseList(id);
        return RespBean.success("进货订单删除成功");
    }

    /**
     * 结算进货订单
     * @param id
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(Integer id) {
        purchaseListService.updatePurchaseList(id);
        return RespBean.success("结算成功");
    }

    @RequestMapping("countPurchase")
    @ResponseBody
    public Map<String,Object > countPurchase(PurchaseListQuery purchaseListQuery){
        return purchaseListService.countPurchase(purchaseListQuery);
    }



}
