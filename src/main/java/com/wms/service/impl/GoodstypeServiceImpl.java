package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Goodstype;
import com.wms.entity.Storage;
import com.wms.mapper.GoodstypeMapper;
import com.wms.mapper.StorageMapper;
import com.wms.service.GoodstypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class GoodstypeServiceImpl extends ServiceImpl<GoodstypeMapper, Goodstype> implements GoodstypeService {
    @Resource
    private GoodstypeMapper goodstypeMapper;

    @Override
    public IPage pageCC(IPage<Goodstype> page, Wrapper wrapper) {
        return goodstypeMapper.pageCC(page, wrapper);
    }

    //获取商品类型ID与名称的映射关系
    //通过商品类型ID查找对应的名称。
    @Override
    public Map<Integer, String> getGoodsTypeMap() {
        List<Goodstype> goodsTypes = goodstypeMapper.selectList(null);
        return goodsTypes.stream()
                .collect(Collectors.toMap( // 提取ID作为Key  Name作为Value
                        Goodstype::getId,
                        Goodstype::getName
                ));
    }

    //通过商品类型名称反向查找ID
    @Override
    public Map<String, Integer> getGoodsTypeNameToIdMap() {
        List<Goodstype> list = this.list();
        return list.stream().collect(Collectors.toMap(  // 提取Name作为Key  ID作为Value
                Goodstype::getName,
                Goodstype::getId
        ));
    }


}
