package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public IPage pageCC(IPage<Goods> page, Wrapper wrapper) {
        return goodsMapper.pageCC(page,wrapper);
    }

    @Override
    public List<Goods> goodsList() {
        return goodsMapper.goodsList();
    }

    @Override
    public List<Map<String, Object>> getGoodsCountByStorage() {
        return goodsMapper.selectMaps(new QueryWrapper<Goods>()
                .select("storage, SUM(count) as total")
                .groupBy("storage"));
    }

    @Override
    public List<Map<String, Object>> getGoodsCountByType() {
        return goodsMapper.selectMaps(new QueryWrapper<Goods>()
                .select("goodstype, SUM(count) as total")
                .groupBy("goodstype"));
    }

    @Override
    public int getLowStockCount(int threshold) {
        return count(new QueryWrapper<Goods>().lt("count", threshold));
    }

    @Override
    public int getTotalStock() {
        Map<String, Object> result = goodsMapper.selectMaps(new QueryWrapper<Goods>()
                .select("SUM(count) as total")).get(0);
        return result.get("total") != null ? ((Number)result.get("total")).intValue() : 0;
    }

    @Override
    public List<Map<String, Object>> getGoodsCountByGoods() {
        return goodsMapper.selectMaps(new QueryWrapper<Goods>()
                .select("name,SUM(count) as total").groupBy("name"));
    }



}
