package com.tang.admin;

import com.tang.admin.exceptions.ParamsException;
import com.tang.admin.pojo.model.RespBean;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * @Author xiaokaixin
 * @Date 2021/8/1 09:44
 * @Version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(AccessDeniedException.class)
//    public String accessDeniedException(AccessDeniedException e){
//        return "403";
//    }

    @ExceptionHandler(ParamsException.class)
    @ResponseBody
    public RespBean paramsExceptionHandler(ParamsException e){
        return RespBean.error(e.getMsg());
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RespBean exceptionHandler(Exception e){
        return RespBean.error(e.getMessage());
    }
}
