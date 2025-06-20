package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Goodstype;

import java.util.List;
import java.util.Map;


public interface GoodsService extends IService<Goods> {

    IPage pageCC(IPage<Goods> page, Wrapper wrapper);

    List<Goods> goodsList();

    // 按仓库统计商品数量
    List<Map<String, Object>> getGoodsCountByStorage();

    // 按分类统计商品数量
    List<Map<String, Object>> getGoodsCountByType();

    // 获取库存不足的商品数量
    int getLowStockCount(int threshold);

    // 获取库存总量
    int getTotalStock();

    List<Map<String, Object>> getGoodsCountByGoods();
}
