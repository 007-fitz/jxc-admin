package com.tang.admin.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.admin.model.CaptchaImageModel;
import com.tang.admin.model.RespBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * MyFilter
 */
@Component
public class CaptchaCodeFilter01 extends OncePerRequestFilter {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 过滤逻辑
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 只对登录请求进行验证码确认和过滤
        if ("/login".equals(request.getRequestURI()) && "post".equalsIgnoreCase(request.getMethod())) {
            try{
                this.validate(request);
            } catch(SessionAuthenticationException e){
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        objectMapper.writeValueAsString(RespBean.error(e.getMessage()))
                );
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 对用户填写的验证码进行验证
     * @param request
     */
    private void validate(HttpServletRequest request) {
        HttpSession session = request.getSession();

        // 用户填写的验证码
        String code = request.getParameter("captchaCode");
        if (StringUtils.isEmpty(code)) {
            throw new SessionAuthenticationException("请填写验证码!");
        }

        // session中的验证码
        CaptchaImageModel codeInSession = (CaptchaImageModel) session.getAttribute("captcha_key");
        if (codeInSession == null) {
            throw new SessionAuthenticationException("验证码不存在");
        }
        if (codeInSession.isExpired()) {
            throw new SessionAuthenticationException("验证码已过期");
        }

        // 比对验证码
        if (!code.equals(codeInSession.getCode())) {
            throw new SessionAuthenticationException("验证码不正确");
        }
    }
}