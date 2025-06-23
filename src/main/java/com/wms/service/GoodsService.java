package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;


public interface GoodsService extends IService<Goods> {

    //分页查询
    //IPage pageCC(IPage<Goods> page, Wrapper wrapper);
    IPage<Goods> listGoodsPage(Page<Goods> page, Wrapper<Goods> queryWrapper);

    //获取商品列表
    List<Goods> goodsList();

    // 按仓库统计商品数量
    List<Map<String, Object>> getGoodsCountByStorage();

    // 按分类统计商品数量
    List<Map<String, Object>> getGoodsCountByType();

    //按商品名称统计数量
    List<Map<String, Object>> getGoodsCountByGoods();

    // 获取库存不足的商品数量
    int getLowStockCount(int threshold);

    // 获取库存总量
    int getTotalStock();


}
