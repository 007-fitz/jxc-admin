package com.tang.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tang.admin.pojo.SaleList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.admin.pojo.model.CountResultModel;
import com.tang.admin.query.SaleListQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 销售单表 Mapper 接口
 * </p>
 *
 * @author leo
 * @since 2021-09-24
 */
public interface SaleListMapper extends BaseMapper<SaleList> {

    String getNextSaleNumber(String str);

    IPage<SaleList> listSaleList(IPage<SaleList> page, SaleListQuery saleListQuery);

    Long countSaleTotal(@Param("saleListQuery") SaleListQuery saleListQuery);

    List<CountResultModel> saleListQueryList(@Param("saleListQuery") SaleListQuery saleListQuery);

    List<Map<String, Object>> countDaySale(@Param("begin") String begin, @Param("end") String end);

    List<Map<String, Object>> countMonthSale(@Param("begin") String begin, @Param("end") String end);

}
