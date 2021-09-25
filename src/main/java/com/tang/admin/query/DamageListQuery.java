package com.tang.admin.query;

import lombok.Data;

/**
 * @Author xiaokaixin
 * @Date 2021/8/11 10:00
 * @Version 1.0
 */
@Data
public class DamageListQuery extends BaseQuery{
    private String startDate;
    private String endDate;
}
