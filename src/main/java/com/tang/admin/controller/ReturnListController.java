package com.tang.admin.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 退货单表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-23
 */
@Controller
@RequestMapping("/return")
public class ReturnListController {

    @RequestMapping("/index")
    public String index() {
        return "/return/return";
    }

    @RequestMapping("/searchPage")
    public String searchPage() {
        return "/return/return_search";
    }


}
