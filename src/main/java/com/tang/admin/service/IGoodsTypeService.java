package com.tang.admin.service;

import com.tang.admin.pojo.dto.TreeDto;
import com.tang.admin.pojo.GoodsType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品类别表 服务类
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
public interface IGoodsTypeService extends IService<GoodsType> {

    Map<String, Object> listGoodsType();

    void saveGoodsType(GoodsType goodsType);

    GoodsType findGoodsTypeByNameAndPid(String name, Integer pid);

    void deleteGoodsType(Integer id);

    List<TreeDto> queryAllGoodsTypes(Integer typeId);

    List<Integer> queryAllSubTypeIdsByTypeId(Integer typeId);

}
