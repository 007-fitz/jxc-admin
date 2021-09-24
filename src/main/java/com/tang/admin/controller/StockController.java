package com.tang.admin.controller;

import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.Goods;
import com.tang.admin.query.GoodsQuery;
import com.tang.admin.service.IGoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Resource
    private IGoodsService goodsService;

    /**
     * 期初库存主页
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "/stock/stock";
    }

    /**
     * 库存量为0 的商品记录，即未入库商品
     * @param goodsQuery
     * @return
     */
    @RequestMapping("listNoInventoryQuantity")
    @ResponseBody
    public Map<String,Object> listNoInventoryQuantity(GoodsQuery goodsQuery){
        goodsQuery.setType(1);
        return goodsService.goodsList(goodsQuery);
    }

    /**
     * 库存量大于0 的商品记录，即已入库商品
     * @param goodsQuery
     * @return
     */
    @RequestMapping("listHasInventoryQuantity")
    @ResponseBody
    public Map<String,Object> listHasInventoryQuantity(GoodsQuery goodsQuery){
        goodsQuery.setType(2);
        return goodsService.goodsList(goodsQuery);
    }

    /**
     * 给指定商品更新库存信息 页面
     * @param gid
     * @param model
     * @return
     */
    @RequestMapping("/toUpdateGoodsInfoPage")
    public String toUpdateGoodsInfoPage(Integer gid, Model model) {
        if (gid != null) {
            model.addAttribute("goods", goodsService.getById(gid));
        }
        return "/stock/goods_update";
    }

    /**
     * 给指定商品更新库存信息
     * @param goods
     * @return
     */
    @RequestMapping("/updateStock")
    @ResponseBody
    public RespBean updateStock(Goods goods) {
        goodsService.updateStock(goods);
        return RespBean.success("商品库存设置成功");
    }

    /**
     * 将指定商品库存归0
     * @param id
     * @return
     */
    @RequestMapping("/deleteStock")
    @ResponseBody
    public RespBean deleteStock(Integer id){
        goodsService.deleteStock(id);
        return RespBean.success("商品库存删除成功!");
    }


}
