package com.tang.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/report")
public class ReportController {

    @RequestMapping("/countSupplier")
    public String countSupplier() {
        return "/count/supplier";
    }

    @RequestMapping("/countCustomer")
    public String countCustomer() {
        return "/count/customer";
    }

    @RequestMapping("/countPurchase")
    public String countPurchase() {
        return "/count/purchase";
    }

    @RequestMapping("/countSale")
    public String countSale() {
        return "/count/sale";
    }

    @RequestMapping("/countDaySale")
    public String countDaySale() {
        return "/count/day_sale";
    }

    @RequestMapping("/countMonthSale")
    public String countMonthSale() {
        return "/count/month_sale";
    }

}
