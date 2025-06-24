package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Goodstype;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Storage;

import java.util.Map;


public interface GoodstypeService extends IService<Goodstype> {

    IPage queryGoodsTypePageByWrapper(IPage<Goodstype> page, Wrapper wrapper);

    Map<Integer, String> getGoodsTypeMap();

    Map<String, Integer> getGoodsTypeNameToIdMap();

}
