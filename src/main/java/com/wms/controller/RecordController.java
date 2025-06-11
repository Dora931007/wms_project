package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Goods;
import com.wms.entity.Record;
import com.wms.entity.User;
import com.wms.service.GoodsService;
import com.wms.service.RecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;


@RestController
@RequestMapping("/record")
public class RecordController {

    @Resource
    private RecordService recordService;

    @Resource
    private GoodsService goodsService;

    @PostMapping("/save")
    public Result add(@RequestBody Record record){
        Goods goods = goodsService.getById(record.getGoods());
        int n = record.getCount();

        if("2".equals(record.getAction())){
            n = -n;
            record.setCount(n);
        }
        int num = goods.getCount()+n;
        goods.setCount(num);
        goodsService.updateById(goods);
        return recordService.save(record)?Result.suc():Result.fail();
    }


    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam queryPageParam){
        HashMap param = queryPageParam.getParam();
        String name = (String)param.get("name");
        String goodsType = (String)param.get("goodstype");
        String storage = (String)param.get("storage");
        String roleId = (String)param.get("roleId");
        String userId = (String)param.get("userId");
        Page<Record> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        QueryWrapper<Record> queryWrapper = new QueryWrapper();
        queryWrapper.apply(" a.goods=b.id and b.storage=c.id and b.goodsType=d.id ");
        if("2".equals(roleId)){
            queryWrapper.apply(" a.user_id= "+userId);
        }
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            queryWrapper.like("b.name",name);
        }
        if(StringUtils.isNotBlank(goodsType) && !"null".equals(goodsType)){
            queryWrapper.eq("d.id",goodsType);
        }
        if(StringUtils.isNotBlank(storage) && !"null".equals(storage)){
            queryWrapper.eq("c.id",storage);
        }
        IPage result = recordService.pageCC(page,queryWrapper);
        recordService.pageCC(page,queryWrapper);
        return Result.suc(result.getRecords(),result.getTotal());
    }

}
