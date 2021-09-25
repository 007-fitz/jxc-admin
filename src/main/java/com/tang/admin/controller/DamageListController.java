package com.tang.admin.controller;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tang.admin.pojo.DamageList;
import com.tang.admin.pojo.DamageListGoods;
import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.query.DamageListQuery;
import com.tang.admin.service.IDamageListService;
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
 * 报损单表 前端控制器
 * 商品报损，系统数据中虚高 现实中商品数量更低。故进行校正（库存减）
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Controller
@RequestMapping("/damage")
public class DamageListController {

    @Resource
    private IDamageListService damageListService;

    /**
     * 创建商品报损记录单 页面
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("damageNumber", damageListService.getNextDamageNumber());
        return "/damage/damage";
    }

    /**
     * 创建商品报损记录单
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Principal principal, DamageList damageList, String goodsJson) {
        Gson gson = new Gson();
        List<DamageListGoods> dlgList = gson.fromJson(goodsJson, new TypeToken<List<DamageListGoods>>() {}.getType());
        damageListService.saveDamageList(principal.getName(), damageList, dlgList);
        return RespBean.success("商品报损记录成功");
    }

    /**
     * 报损单的展示
     * @param damageListQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(DamageListQuery damageListQuery) {
        return damageListService.listDamageList(damageListQuery);
    }

    /**
     * 删除报损单
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        damageListService.deleteDamageList(id);
        return RespBean.success("报损单删除成功");
    }




}
