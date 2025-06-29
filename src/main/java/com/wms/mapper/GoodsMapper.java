package com.wms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.wms.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 分页查询商品列表
     * @param page 分页参数(当前页、每页数量)
     * @param wrapper 查询条件构造器
     */
    IPage<Goods> listGoodsPage(IPage<Goods> page, @Param(Constants.WRAPPER) Wrapper<Goods> wrapper);

    /**
     * 获取所有商品列表
     */
    List<Goods> goodsList();

    /**
     * 按仓库统计商品数量
     */
    @MapKey("storage")// 指定storage字段作为Map的key
    List<Map<String, Object>> getGoodsCountByStorage();

    /**
     * 按商品分类统计数量
     */
    @MapKey("goodstype")
    List<Map<String, Object>> getGoodsCountByType();

    /**
     * 按商品名称统计数量
     */
    @MapKey("name")
    List<Map<String, Object>> getGoodsCountByGoods();

    /**
     * 获取库存低于阈值的商品数量
     * @param threshold 库存阈值
     */
    int getLowStockCount(int threshold);

    /**
     * 获取库存总量
     */
    int getTotalStock();

}
