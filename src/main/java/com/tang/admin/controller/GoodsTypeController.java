package com.tang.admin.controller;


import com.tang.admin.pojo.dto.TreeDto;
import com.tang.admin.pojo.model.RespBean;
import com.tang.admin.pojo.GoodsType;
import com.tang.admin.service.IGoodsTypeService;
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

    /**
     * 商品类别展示 页面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "/goodsType/goods_type";
    }

    /**
     * 商品类别展示
     * @return layui层级显示所需数据格式
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list() {
        return goodsTypeService.listGoodsType();
    }

    /**
     * 新增商品类别 页面
     * @param pId
     * @param model
     * @return
     */
    @RequestMapping("/addGoodsTypePage")
    public String addGoodsTypePage(Integer pId, Model model) {
        if (pId != null) {
            model.addAttribute("pId", pId);
        }
        return "/goodsType/add";
    }

    /**
     * 新增商品类别
     * @param goodsType
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(GoodsType goodsType){
        goodsTypeService.saveGoodsType(goodsType);
        return RespBean.success("商品类别记录添加成功");
    }

    /**
     * 删除商品类别
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer id) {
        goodsTypeService.deleteGoodsType(id);
        return RespBean.success("商品类别记录删除成功");
    }

    /**
     * 商品类别展示(简约版)
     * @param typeId
     * @return zTree层级展示所需数据格式 使用TreeDto封装
     */
    @RequestMapping("queryAllGoodsTypes")
    @ResponseBody
    public List<TreeDto> queryAllGoodsTypes(Integer typeId){
        return goodsTypeService.queryAllGoodsTypes(typeId);
    }

}
