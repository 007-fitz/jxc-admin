package com.tang.admin.controller;

import com.tang.admin.pojo.User;
import com.tang.admin.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.events.Event;

import javax.annotation.Resource;
import java.security.Principal;

@Controller
public class MainController {

    @Resource
    private IUserService userService;

    /**
     * 主页面 01
     * 从principal中拿用户id，去数据库中查询用户最新信息，页面通过model去获取数据
     * @return 主页面
     */
//    @RequestMapping("/main")
    public String main(Principal principal, Model model) {
        // principal----org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        // Principal接口 是 Authentication接口 的父接口，这里注入的是Authentication实现类实例对象。
        // 其中一个方法getPrincipal能返回UserDetails对象
        // 获取id
        Authentication authentication = (Authentication) principal;
        int uid = ((User)authentication.getPrincipal()).getId();
        // 根据id查用户，结果放在model中，进行视图解析
        User user = userService.getById(uid);
        model.addAttribute("user", user);
        return "main";
    }

    /**
     * 主页面 02
     * 每次更新数据，更新session中的用户信息，保证数据最新。页面去session中获取数据
     * @return 主页面
     */
    @RequestMapping("/main")
    public String main2() {
        return "main";
    }

    /**
     * 系统登录页面
     * @return 视图
     */
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 系统欢迎页面
     * @return 视图
     */
    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }


}
