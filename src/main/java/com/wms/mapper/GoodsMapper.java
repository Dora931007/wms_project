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

    IPage<Goods> listGoodsPage(IPage<Goods> page, @Param(Constants.WRAPPER) Wrapper<Goods> wrapper);

    List<Goods> goodsList();

    @MapKey("storage")
        // 指定storage字段作为Map的key
    List<Map<String, Object>> getGoodsCountByStorage();

    @MapKey("goodstype")
    List<Map<String, Object>> getGoodsCountByType();

    @MapKey("name")
    List<Map<String, Object>> getGoodsCountByGoods();

    int getLowStockCount(int threshold);

    int getTotalStock();

}
