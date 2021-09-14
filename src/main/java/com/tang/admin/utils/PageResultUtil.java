package com.tang.admin.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \
 *
 * @author xiaokaixin
 * @version 1.0
 */
public class PageResultUtil {

    public static Map<String,Object> getResult(Long total, List<?> records){
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("count",total);
        result.put("data",records);
        result.put("code",0);
        result.put("msg","");
        return result;
    }
}
