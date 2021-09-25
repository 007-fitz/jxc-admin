package com.tang.admin.controller;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tang.admin.pojo.OverflowList;
import com.tang.admin.pojo.OverflowListGoods;
import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.query.OverFlowListQuery;
import com.tang.admin.service.IOverflowListService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报溢单表 前端控制器
 * 商品报溢，系统数据中偏低 现实中商品数量更高。故进行校正（库存加）
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Controller
@RequestMapping("/overflow")
public class OverflowListController {

    @Resource
    private IOverflowListService overflowListService;

    /**
     * 创建商品报溢单 页面
     *
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("overflowNumber", overflowListService.getOverflowNumber());
        return "/overflow/overflow";
    }

    /**
     * 新增报溢单
     * @param principal
     * @param overflowList
     * @param goodsJson
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Principal principal, OverflowList overflowList, String goodsJson) {
        Gson gson = new Gson();
        List<OverflowListGoods> olgList = gson.fromJson(goodsJson, new TypeToken<List<OverflowListGoods>>() {
        }.getType());
        overflowListService.saveOverflowList(principal.getName(), overflowList, olgList);
        return RespBean.success("商品报溢单记录成功");
    }

    /**
     * 报溢单展示
     * @param overFlowListQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> overFlowList(OverFlowListQuery overFlowListQuery){
        return overflowListService.listOverFlowList(overFlowListQuery);
    }

    /**
     * 删除报溢单记录
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id){
        overflowListService.deleteOverflowList(id);
        return RespBean.success("删除成功");
    }


}
