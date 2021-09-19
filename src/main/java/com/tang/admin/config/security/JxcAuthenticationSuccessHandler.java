package com.tang.admin.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.admin.model.RespBean;
import com.tang.admin.pojo.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 小开心
 * @version 1.0
 */
@Component
public class JxcAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 将用户部分信息暂存在session中，方便页面信息的展示和更新,也避免重复去访问数据库最新信息。
        User userDetails = (User) authentication.getPrincipal();
        userDetails.setPassword(null);
        request.getSession().setAttribute("currentUser", userDetails);
        // 准备响应数据
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                RespBean.success("登录成功")));
    }
}
