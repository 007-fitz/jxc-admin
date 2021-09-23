package com.tang.admin.controller;


import com.tang.admin.dto.TreeDto;
import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.GoodsType;
import com.tang.admin.pojo.Supplier;
import com.tang.admin.service.IGoodsTypeService;
import com.tang.admin.service.ISupplierService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品类别表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
@Controller
@RequestMapping("/goodsType")
public class GoodsTypeController {

    @Resource
    private IGoodsTypeService goodsTypeService;

    @RequestMapping("/index")
    public String index() {
        return "/goodsType/goods_type";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list() {
        return goodsTypeService.listGoodsType();
    }

    @RequestMapping("/addGoodsTypePage")
    public String addGoodsTypePage(Integer pId, Model model) {
        if (pId != null) {
            model.addAttribute("pId", pId);
        }
        return "/goodsType/add";
    }

    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(GoodsType goodsType){
        goodsTypeService.saveGoodsType(goodsType);
        return RespBean.success("商品类别记录添加成功");
    }

    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        goodsTypeService.deleteGoodsType(id);
        return RespBean.success("商品类别记录删除成功");
    }

    @RequestMapping("queryAllGoodsTypes")
    @ResponseBody
    public List<TreeDto> queryAllGoodsTypes(Integer typeId){
        return goodsTypeService.queryAllGoodsTypes(typeId);
    }

}
