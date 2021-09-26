package com.tang.admin.pojo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 乐字节  踏实教育 用心服务
 *  商品采购|销售统计Model
 * @author 乐字节--老李
 * @version 1.0
 */
@Data
public class CountResultModel {
    @ApiModelProperty(value = "单号")
    private String number;

    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;

    @ApiModelProperty(value = "供应商|客户名称")
    private String name;

    @ApiModelProperty(value = "商品编码")
    private String code;

    @ApiModelProperty(value = "商品型号")
    private String model;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品类别")
    private String typeName;


    @ApiModelProperty(value = "单位")
    private String unitName;

    @ApiModelProperty(value = "单价")
    private Float price;

    @ApiModelProperty(value = "数量")
    private Integer num;


    @ApiModelProperty(value = "总价")
    private Float total;


}
