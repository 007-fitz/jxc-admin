package com.tang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.admin.pojo.dto.TreeDto;
import com.tang.admin.pojo.Goods;
import com.tang.admin.pojo.GoodsType;
import com.tang.admin.mapper.GoodsTypeMapper;
import com.tang.admin.service.IGoodsService;
import com.tang.admin.service.IGoodsTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.admin.utils.AssertUtil;
import com.tang.admin.utils.PageResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品类别表 服务实现类
 * </p>
 *
 * @author leo
 * @since 2021-09-22
 */
@Service
public class GoodsTypeServiceImpl extends ServiceImpl<GoodsTypeMapper, GoodsType> implements IGoodsTypeService {

    @Resource
    private IGoodsService goodsService;

    /**
     * 层级展示所有商品类别
     * @return layui层级展示所需数据格式
     */
    @Override
    public Map<String, Object> listGoodsType() {
        List<GoodsType> list = this.list();
        return PageResultUtil.getResult((long) list.size(), list);
    }

    /**
     * 新增商品类别
     * @param goodsType
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveGoodsType(GoodsType goodsType) {
        AssertUtil.isTrue(StringUtils.isBlank(goodsType.getName()), "商品类别名称不能为空");
        AssertUtil.isTrue(null == goodsType.getPId(), "请指定父类别");
        AssertUtil.isTrue(null != this.findGoodsTypeByNameAndPid(goodsType.getName(), goodsType.getPId()), "同父节点下 商品类别名已存在");
        goodsType.setState(0);
        AssertUtil.isTrue(!this.save(goodsType), "商品类别添加失败");
        // 父节点状态更新
        GoodsType parent = this.getOne(new QueryWrapper<GoodsType>().eq("id", goodsType.getPId()));
        if (parent.getState() == 0) {
            parent.setState(1);
            AssertUtil.isTrue(!this.updateById(parent), "商品类别添加失败");
        }
    }

    /**
     * 在指定父节点下，查找指定名称的商品类别
     * @param name
     * @param pid
     * @return
     */
    @Override
    public GoodsType findGoodsTypeByNameAndPid(String name, Integer pid) {
        return this.getOne(new QueryWrapper<GoodsType>().eq("name", name).eq("p_id", pid));
    }

    /**
     * 删除商品类别
     * @param id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteGoodsType(Integer id) {
        GoodsType temp = this.getById(id);
        AssertUtil.isTrue(null == temp, "待删除记录不存在");
        AssertUtil.isTrue(temp.getState()==1, "该节点存在子节点，不能删除");
        // 与商品的外键关联，如果被某个商品关联，则不能删除
        int relationCount = goodsService.count(new QueryWrapper<Goods>().eq("is_del", 0).eq("type_id", id));
        AssertUtil.isTrue(relationCount>0, "存在商品所属于当前类别，类别记录不能删除");
        // 当前节点父节点的下级计数，即同层级总数
        int count = this.count(new QueryWrapper<GoodsType>().eq("p_id", temp.getPId()));
        // 更新父节点状态
        if (count == 1) {
//            AssertUtil.isTrue(!(this.update(new UpdateWrapper<GoodsType>().set("state",0).eq("id",temp.getPId()))),"类别删除失败!");
            GoodsType parent = this.getById(temp.getPId());
            parent.setState(0);
            AssertUtil.isTrue(!this.updateById(parent), "记录删除失败");
        }
        // 删除当前节点
        AssertUtil.isTrue(!this.removeById(id), "记录删除失败");
    }

    /**
     * 层级展示所有商品类别(简约版)
     * @param typeId 如果非空，则设置checked属性为真，以回显
     * @return zTree层级展示所需数据格式
     */
    @Override
    public List<TreeDto> queryAllGoodsTypes(Integer typeId) {
        List<TreeDto> treeDtos = this.baseMapper.queryAllGoodsTypes(typeId);
        // 设置节点选中
        if(null != typeId){
            for (TreeDto treeDto : treeDtos) {
                if(treeDto.getId().equals(typeId)){
                    treeDto.setChecked(true);
                    break;
                }
            }
        }
        return treeDtos;
    }

    /**
     * 根据指定商品类别id，查找其下所有子节点
     * @param typeId
     * @return 该节点及所有子节点的id
     */
    @Override
    public List<Integer> queryAllSubTypeIdsByTypeId(Integer typeId) {
        if(typeId==1){
            // 所有类别
            return this.list().stream().map(GoodsType::getId).collect(Collectors.toList());
        }
        ArrayList<Integer> result = new ArrayList<>();
        result.add(typeId);
        this.getSubAndAdd(typeId, result);
        return result;
    }

    /**
     * 递归函数  查找指定节点下的所有子节点，对每个子节点都 加入集合，同时递归将该子节点下的所有节点加入集合
     * @param typeId
     * @param result
     */
    private void getSubAndAdd(Integer typeId, ArrayList<Integer> result) {
        List<GoodsType> subs = this.baseMapper.selectList(new QueryWrapper<GoodsType>().eq("p_id", typeId));
        if (subs != null) {
            subs.forEach(sub ->{
                result.add(sub.getId());
                getSubAndAdd(sub.getId(), result);
            });
        }
    }
}
