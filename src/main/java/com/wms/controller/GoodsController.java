package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Goods;
import com.wms.entity.Goodstype;
import com.wms.entity.User;
import com.wms.service.GoodsService;
import com.wms.service.GoodstypeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    @PostMapping("/save")
    public Result add(@RequestBody Goods goods){
        System.out.println("Save goods data: " + goods);
        return goodsService.save(goods)?Result.suc():Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods){
        return goodsService.updateById(goods)?Result.suc():Result.fail();
    }

    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return goodsService.removeById(id)?Result.suc():Result.fail();
    }

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam queryPageParam){
        HashMap param = queryPageParam.getParam();
        String name = (String)param.get("name");
        String goodsType = (String)param.get("goodstype");
        String storage = (String)param.get("storage");

        Page<Goods> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(Goods::getName,name);
        }
        if(StringUtils.isNotBlank(goodsType) && !"null".equals(goodsType)){
            lambdaQueryWrapper.eq(Goods::getGoodstype,goodsType);
        }
        if(StringUtils.isNotBlank(storage) && !"null".equals(storage)){
            lambdaQueryWrapper.eq(Goods::getStorage,storage);
        }

        IPage result = goodsService.pageCC(page,lambdaQueryWrapper);
        goodsService.pageCC(page,lambdaQueryWrapper);


        return Result.suc(result.getRecords(),result.getTotal());
    }


}
