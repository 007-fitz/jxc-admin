package com.tang.admin.query;


import lombok.Data;

import java.util.List;

/**
 * 乐字节  踏实教育 用心服务
 *
 * @author 乐字节--老李
 * @version 1.0
 */
@Data
public class GoodsQuery extends BaseQuery{

    private String goodsName;
    private Integer typeId;

    private List<Integer> typeIds;

    //查旋类型 区分库存量是否大于0
    /**
     * type == 1 库存量=0
     *2 >0
     * 3 库存量<库存下限
     */
    private Integer type;

}
