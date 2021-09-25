package com.tang.admin.controller;

import com.tang.admin.query.GoodsQuery;
import com.tang.admin.service.IGoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/common")
public class CommonController {
    @Resource
    private IGoodsService goodsService;

    /**
     * 创建订单时，展示所有商品以供选择 的页面
     * @return
     */
    @RequestMapping("/toSelectGoodsPage")
    public String toSelectGoodsPage() {
        return "/common/goods";
    }

    /**
     * 选中指定商品进行添加入库页面
     * @param gid
     * @param model
     * @return
     */
    @RequestMapping("/toAddGoodsInfoPage")
    public String toAddGoodsInfoPage(Integer gid, Model model) {
        if (gid != null) {
            model.addAttribute("goods", goodsService.getGoodsInfoById(gid));
        }
        return "/common/goods_add_update";
    }

    /**
     * 对指定商品入库信息进行更新页面
     * @param id
     * @param price
     * @param num
     * @param model
     * @return
     */
    @RequestMapping("/toUpdateGoodsInfoPage")
    public String toUpdateGoodsInfoPage(Integer id, String price, Integer num, Model model) {
        if (id != null) {
            model.addAttribute("goods", goodsService.getGoodsInfoById(id));
        }
        if (price != null) {
            model.addAttribute("price", price);
        }
        if (num != null) {
            model.addAttribute("num", num);
        }
        model.addAttribute("flag", 1);
        return "/common/goods_add_update";
    }

    /**
     * 商品库存展示 页面
     * @return
     */
    @RequestMapping("/toGoodsStockPage")
    public String toGoodsStockPage() {
        return "/common/stock_search";
    }

    /**
     * 展示库存（其实就是商品展示）
     * @param goodsQuery
     * @return
     */
    @RequestMapping("/stockList")
    @ResponseBody
    public Map<String, Object> stockList(GoodsQuery goodsQuery) {
        return goodsService.stockList(goodsQuery);
    }

    /**
     * 库存报警展示 页面
     * @return
     */
    @RequestMapping("/alarmPage")
    public String alarmPage() {
        return "/common/alarm";
    }

    /**
     * 库存报警展示（同样也是商品展示，只不过条件性进行展示：只展示库存量低于预设预警值的商品）
     * @param goodsQuery
     * @return
     */
    @RequestMapping("/listAlarm")
    @ResponseBody
    public Map<String, Object> listAlarm(GoodsQuery goodsQuery) {
        goodsQuery.setType(3);
        return goodsService.goodsList(goodsQuery);
    }

    @RequestMapping("/toDamageOverflowSearchPage")
    public String toDamageOverflowSearchPage() {
        return "/common/damage_overflow_search";
    }
}

