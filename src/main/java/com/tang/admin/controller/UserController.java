package com.tang.admin.controller;


import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.User;
import com.tang.admin.query.UserQuery;
import com.tang.admin.service.IUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author leo
 * @since 2021-09-12
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 修改密码页面
     *
     * @return 修改密码页面
     */
    @RequestMapping("/password")
    public String password() {
        return "/user/password";
    }

    /**
     * 修改用户密码
     *
     * @param session         用于获取id
     * @param oldPassword     旧密码
     * @param newPassword     新密码
     * @param confirmPassword 确认密码
     * @return 响应消息
     */
    @RequestMapping("/updateUserPassword")
    @ResponseBody
    public RespBean updateUserPassword(HttpSession session, String oldPassword, String newPassword, String confirmPassword) {
        User user = (User) session.getAttribute("currentUser");
        userService.updatePassword(user.getId(), oldPassword, newPassword, confirmPassword);
        return RespBean.success("密码修改成功");
    }


    /**
     * 修改信息页面 02
     * 每次用户信息修改之后，手动更新session中的用户信息，来保证数据最新，页面通过session拿数据
     *
     * @return 修改用户信息页面
     */
    @RequestMapping("/setting")
    public String setting2() {
        return "/user/setting";
    }

    /**
     * 修改用户信息
     *
     * @param user 用户准备好的数据，其中隐式包含id
     * @return 响应消息
     */
    @RequestMapping("/updateUserInfo")
    @ResponseBody
    public RespBean updateUserInfo(User user, HttpSession session) {
        userService.updateUserInfo(user);
        this.updateUserInfoInSessionByUid(user.getId(), session);
        return RespBean.success("用户信息修改成功");
    }

    /**
     * 根据id查找用户最新信息，并放在session中
     * 虽然框架中已经有了相应的对象，但是为了操作方便，另存了一份
     *
     * @param id      用户id，用于查找user对象
     * @param session 将查找到的user放在session中
     */
    private void updateUserInfoInSessionByUid(Integer id, HttpSession session) {
        User user = userService.getById(id);
        user.setPassword(null);
        session.setAttribute("currentUser", user);
    }

    /**
     * 用户列表页面
     *
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "/user/user";
    }

    /**
     * 用户信息全展示、模糊匹配展示
     *
     * @param userQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(UserQuery userQuery) {
        return userService.userList(userQuery);
    }


    /**
     * 用户记录增加/更新 页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id, Model model) {
        if (null != id) {
            User user = userService.getById(id);
            model.addAttribute("user", user);
        }
        return "/user/add_update";
    }

    /**
     * 新增用户记录
     *
     * @param user
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public RespBean save(User user) {
        userService.saveUser(user);
        return RespBean.success("用户新增成功");
    }

    /**
     * 更新用户记录
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(User user, Principal principal, HttpSession session) {
        userService.updateUser(user);
        // session 中用户数据一致性维护
        Integer currentUid = ((User) ((Authentication) principal).getPrincipal()).getId();
        if (currentUid.equals(user.getId())) {
            this.updateUserInfoInSessionByUid(currentUid, session);
        }
        return RespBean.success("用户更新成功");
    }

    /**
     * 删除用户s
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RespBean delete(Integer[] ids) {
        userService.deleteUsers(ids);
        return RespBean.success("用户删除成功");
    }


    /**
     * 修改信息页面 01
     * 每次通过url中的用户id去查数据库最新信息，页面从model中拿数据
     * @return 修改用户信息页面
     */
////    @RequestMapping("/setting/{id}")
//    public String setting(@PathVariable("id") int id, Model model) {
//        // 在未使用框架时，用户登录之后，用户信息user保存在session域当中，
//        // 当前端想要访问用户信息时，要么去session中拿user，要么临时去数据库当中查user。
//        //      如果，去session域中拿，但是 我们要保证在数据库数据修改之后，session域中的用户信息也能够修改
//        //      如果，临时去数据库中拿，是最新的数据，但是需要访问数据库
//        // 引入框架之后，大体流程相同，框架将authentication对象放在session中，其中封装了user
//        // 但是，我们不能够直接操作这个对象，不方便修改和新建。
//        // 所以这里我们使用第二种方式，临时去查。去principal中拿用户名，根据用户名去数据库中查。
//        // 2.0  优化，principal在数据库过更新之后，没有更新，所以拿到的用户名也是旧的，
//        // 所以不是拿用户名，而是去拿id。可以去principal中拿id，类似 /main；也可通过前端保存的id放在url中，后端通过url去拿
//        User user = userService.getById(id);
//        model.addAttribute("user", user);
//        return "/user/setting";
//    }
}
