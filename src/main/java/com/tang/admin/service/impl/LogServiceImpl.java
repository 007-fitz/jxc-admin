package com.tang.admin.service.impl;

import com.tang.admin.pojo.Log;
import com.tang.admin.mapper.LogMapper;
import com.tang.admin.service.ILogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-25
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

}
