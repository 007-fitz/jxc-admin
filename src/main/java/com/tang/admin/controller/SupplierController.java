package com.tang.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.Supplier;
import com.tang.admin.query.SupplierQuery;
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
 * 供应商表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
@Controller
@RequestMapping("/supplier")
public class SupplierController {

    @Resource
    private ISupplierService supplierService;

    @RequestMapping("/index")
    public String index() {
        return "supplier/supplier";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(SupplierQuery supplierQuery) {
        return supplierService.listSupplier(supplierQuery);
    }

    @RequestMapping("/addOrUpdateSupplierPage")
    public String addOrUpdateSupplierPage(Integer id, Model model) {
        if (id != null) {
            model.addAttribute("supplier", supplierService.getById(id));
        }
        return "/supplier/add_update";
    }


    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(Supplier supplier){
        supplierService.saveSupplier(supplier);
        return RespBean.success("供应商新增成功");
    }

    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(Supplier supplier){
        supplierService.updateSupplier(supplier);
        return RespBean.success("供应商更新成功");
    }

    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer[] ids) {
        supplierService.deleteSupplier(ids);
        return RespBean.success("供应商删除成功");
    }

    @RequestMapping("/allGoodsSuppliers")
    @ResponseBody
    public List<Supplier> allGoodsSuppliers() {
        return supplierService.list(new QueryWrapper<Supplier>().eq("is_del",0));
    }

}
