package com.tang.admin.controller;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tang.admin.pojo.PurchaseListGoods;
import com.tang.admin.pojo.ReturnList;
import com.tang.admin.pojo.ReturnListGoods;
import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.query.ReturnListQuery;
import com.tang.admin.service.IReturnListService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 退货单表 前端控制器
 * (仓库中多余的商品进行退货，多个商品合并为一个退货单，退给某供应商)
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/return")
public class ReturnListController {

    @Resource
    private IReturnListService returnListService;

    /**
     * 退货出库订单创建 页面
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("returnNumber", returnListService.getNextReturnNumber());
        return "/return/return";
    }

    /**
     * 创建出库单据
     * @param principal
     * @param returnList
     * @param goodsJson
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Principal principal, ReturnList returnList, String goodsJson) {
        Gson gson = new Gson();
        List<ReturnListGoods> rlgList =gson.fromJson(goodsJson,new TypeToken<List<ReturnListGoods>>(){}.getType());
        returnListService.saveReturnList(principal.getName(), returnList, rlgList);
        return RespBean.success("出库退货单据创建成功");
    }

    /**
     * 退货出库订单展示 页面
     * @return
     */
    @RequestMapping("/searchPage")
    public String searchPage() {
        return "/return/return_search";
    }

    /**
     * 条件性 展示退货出库订单
     * @param returnListQuery
     * @return 分页查询结果
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(ReturnListQuery returnListQuery) {
        return returnListService.listReturnList(returnListQuery);
    }

    /**
     * 删除退货出库订单
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        returnListService.deleteReturnListService(id);
        return RespBean.success("退货出库订单删除失败");
    }

}
