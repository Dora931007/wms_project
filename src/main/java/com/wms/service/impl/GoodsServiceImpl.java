package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.entity.Goods;
import com.wms.entity.Goodstype;
import com.wms.mapper.GoodsMapper;
import com.wms.mapper.GoodstypeMapper;
import com.wms.service.GoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public IPage<Goods> listGoodsPage(Page<Goods> page, Wrapper<Goods> queryWrapper) {
        return goodsMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Goods> goodsList() {
        return goodsMapper.goodsList();
    }

    @Override
    public List<Map<String, Object>> getGoodsCountByStorage() {
        return goodsMapper.getGoodsCountByStorage();
    }

    @Override
    public List<Map<String, Object>> getGoodsCountByType() {
        return goodsMapper.getGoodsCountByType();
    }

    @Override
    public List<Map<String, Object>> getGoodsCountByGoods() {
        return goodsMapper.getGoodsCountByGoods();
    }

    @Override
    public int getLowStockCount(int threshold) {
        return goodsMapper.getLowStockCount(threshold);
    }

    @Override
    public int getTotalStock() {
        return goodsMapper.getTotalStock();
    }

}
