package com.tang.admin.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.tang.admin.pojo.model.CaptchaImageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class CaptchaController {

    @Resource
    private DefaultKaptcha defaultKaptcha;

    /**
     * 生成并返回验证码图片
     * @param response
     * @param session
     * @throws IOException
     */
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public void kaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 准备好验证码和对应图片
        String text = defaultKaptcha.createText();
        BufferedImage bufferedImage = defaultKaptcha.createImage(text);
        // 将图片输出
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bufferedImage, "jpg", out);
        out.flush();

        // 保存验证码
        session.setAttribute("captcha_key", new CaptchaImageModel(text, 2*60));
    }

}
