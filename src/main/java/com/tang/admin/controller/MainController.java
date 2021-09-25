package com.tang.admin.controller;

import com.tang.admin.pojo.User;
import com.tang.admin.service.IUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class MainController {

    @Resource
    private IUserService userService;

    /**
     * 主页面
     * 我们在登录successHandler中手动的将用户信息存在了session中，
     * 并保证在用户信息更新时，同步更新session中的数据，这样main页面能直接从session中读数据，而不用每次去数据库中查(去principal中拿uid，或者url中传uid)。
     *      但是通过rememberMe的方式，不会走Handler的响应处理，所以session中没有数据。
     *      而框架的rememberMe，当rememberMe成功时，能够将authentication(principal)对象恢复，
     *      所以两种方式结合，当session中有数据时，去session中拿，当没有时，临时通过principal去查。
     *
     * 从principal中拿用户id，去数据库中查询用户最新信息，页面通过model去获取数据
     * @return 主页面
     */
    @RequestMapping("/main")
    public String main(Principal principal, HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "main";
        }
        this.recoverSessionUserInfoFromPrincipal(principal, session);
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

    /**
     * 设置项目默认主页
     * @return 重定向到main主页
     */
    @RequestMapping("/")
    public String defaultPage() {
        return "redirect:/main";
    }

    /**
     * 根据当前用户的id，查找并获得user对象，放在Session域中
     * @param principal
     * @param session
     */
    private void recoverSessionUserInfoFromPrincipal(Principal principal, HttpSession session) {
        // principal----org.springframework.security.authentication.UsernamePasswordAuthenticationToken
        // Principal接口 是 Authentication接口 的父接口，这里注入的是Authentication实现类实例对象。
        // 其中Authentication接口的一个方法getPrincipal能返回UserDetails对象
        // 通过UserDetails获取id。因为这个对象是自己的，通过实现接口的方式，而没有用框架自带的
        System.out.println(principal);    // rememberMe成功后，框架自动恢复了principal对象
        Authentication authentication = (Authentication) principal;
        int uid = ((User)authentication.getPrincipal()).getId();
        // 根据id查用户，结果放在model中，进行视图解析
        User user = userService.getById(uid);
        // 会走这条路说明session中没有数据了，手动保存起来
        session.setAttribute("currentUser", user);
    }


    /**
     * 获得principal，做测试用接口
     * @param principal
     * @return
     */
    @RequestMapping("/getPrincipal")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_管理员')")
    public Object principal(Principal principal) {
        return principal;
    }



    /* 用户登录总结
        未使用框架时：
           1. /login.html,/login不拦截，其余拦截
           2. 走/login，-> Service层验证用户
           3. 验证成功 ? 保存user到session中，响应 : 抛异常，接异常，响应

           - 页面用户信息：
                - 去 session > user 中拿
                - 临时去数据库查 (在session中有的情况下，没必要再去数据库查)
           - 当用户数据更新时，同步更新session中信息。保证数据最新，避免去数据库查

           - rememberMe
           - session域等，服务器一关，数据就没了，所以为了实现记住我，数据不能存在session中，而且当session没数据的时候(未登录)，能够重构登录信息
           -    直接将用户名密码保存在cookie中，当检测到这个cookie，直接隐式登录
           -    将用户名密码存在数据库中，分配令牌，让用户cookie保存令牌 （重要信息保存在了数据库的一张表当中，拿着这个令牌相当于拿着用户名和密码)

        使用框架之后：
            框架虽然主要通过Filter实现，但是整体的体系结构，流程还是模拟了的，依然有类似Control，Service层中负责的事
            三部分：
                1. 在配置类中配置url等 ---------------> 相当于Controller层的请求映射
                2. 自定义登录逻辑，数据库拿对象 ---------> 相当于Service层的验证 （对密码的验证）
                3. 用户验证成功与否 ?         ---------> 相当于Control层去产生视图/响应
                      将user封装为Authentication，放入Session中，走SuccessHandler响应
                      : 抛异常，接异常，走FailureHandler响应

            - 页面用户信息
                - 去 session > principal 中拿  ------(更新不及时)
                - 临时去数据库查。               ------(每次都去数据库不好，查询依据来自于principal中的uid，或者前端访问principal之后，uid放在url中)
                      因为这个对象不是我们直接操控的，所以当用户信息更新之后，这个对象中信息不是最新，所以可能需要去数据库中查
            - 第一种不能手动更新，第二种每加载一次页面，都去查数据库，所以
              手动创建user，类似之前未使用框架时，框架依旧走authentication，user只是用于信息展示
                - 当登录成功之后，在SuccessHandler中手动将自定义user保存在Session当中
                - 当用户数据更新时，同步更新session中信息

            - rememberMe
                以数据库存信息，用户存令牌的方式。
                --- 当用户选中了记住我时，将登录时用户名和随机令牌 绑定保存
                --- 当用户拿着令牌来时，能够根据用户名，去loadUser，重构 session 中的 authentication 对象  (RememberMeAuthenticationToken)
                - 注意，只是走了loadUserByUsername，去加载user，重构Authentication，
                        并不会走登录时的successHandler，也就不会自动将user存在session当中，页面在session中拿不到用户信息
                - 所以，在访问main、setting等需要session中用户信息的页面时，
                        首先去session中看有没有 ? 有正常访问 : 属于记住我且第一次访问情况，通过重构后的authentication去访问数据库，在session中创建user

                注意，rememberMe 数据库表中保存的是 那个时刻的用户名，当用户名修改之后，rememberMe是不能成功的。且会清cookie

     *
     */

}
