package com.tang.admin.query;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PurchaseListQuery extends BaseQuery {

    private String purchaseNumber;
    private Integer supplierId;
    private Integer state;

    private String startDate;
    private String endDate;

    private String goodsName;
    private Integer typeId;
    private List<Integer> typeIds;

    // 手动分页查询所需参数
    public Integer index;
}
