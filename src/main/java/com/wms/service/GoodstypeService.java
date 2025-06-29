package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Goodstype;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Storage;

import java.util.Map;


public interface GoodstypeService extends IService<Goodstype> {

    IPage queryGoodsTypePageByWrapper(IPage<Goodstype> page, Wrapper wrapper);

    //通过商品类型ID查找对应的名称。
    Map<Integer, String> getGoodsTypeMap();

    //通过商品类型名称反向查找ID
    Map<String, Integer> getGoodsTypeNameToIdMap();

}
