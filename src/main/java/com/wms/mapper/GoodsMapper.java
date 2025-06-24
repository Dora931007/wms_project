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
     * @return 分页结果(包含记录列表和分页信息)
     */
    IPage<Goods> listGoodsPage(IPage<Goods> page, @Param(Constants.WRAPPER) Wrapper<Goods> wrapper);

    /**
     * 获取所有商品列表
     * @return 商品实体列表
     */
    List<Goods> goodsList();

    /**
     * 按仓库统计商品数量
     * @return Map列表，key为仓库ID，value为对应商品总量
     */
    @MapKey("storage")// 指定storage字段作为Map的key
    List<Map<String, Object>> getGoodsCountByStorage();

    /**
     * 按商品分类统计数量
     * @return Map列表，key为分类ID，value为对应商品总量
     */
    @MapKey("goodstype")
    List<Map<String, Object>> getGoodsCountByType();

    /**
     * 按商品名称统计数量
     * @return Map列表，key为商品名称，value为对应总量
     */
    @MapKey("name")
    List<Map<String, Object>> getGoodsCountByGoods();

    /**
     * 获取库存低于阈值的商品数量
     * @param threshold 库存阈值
     * @return 低于阈值的商品种类数
     */
    int getLowStockCount(int threshold);

    /**
     * 获取库存总量
     * @return 所有商品库存数量总和
     */
    int getTotalStock();

}
