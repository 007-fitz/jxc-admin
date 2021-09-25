package com.tang.admin.query;

import lombok.Data;
/**
 * @Author xiaokaixin
 * @Date 2021/8/8 08:24
 * @Version 1.0
 */
@Data
public class CustomerReturnListQuery extends BaseQuery{
    // 退货单号
    private String customerReturnNumber;

    // 客户
    private Integer customerId;

    // 是否付款
    private Integer state;

    // 开始时间
    private String startDate;

    // 结束时间
    private String endDate;


}
