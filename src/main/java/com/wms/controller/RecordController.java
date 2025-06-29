package com.wms.controller;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.*;
import com.wms.service.GoodsService;
import com.wms.service.GoodstypeService;
import com.wms.service.RecordService;
import com.wms.service.StorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/record")
public class RecordController {

    @Resource
    private RecordService recordService;

    @Resource
    private GoodsService goodsService;


    @PostMapping("/save")
    public Result add(@RequestBody Record record) {
        //根据record中的商品ID获取商品完整信息
        Goods goods = goodsService.getById(record.getGoods());
        int n = record.getCount();

        // 如果是出库操作（action="2"），将数量转为负数
        if ("2".equals(record.getAction())) {
            n = -n;
            record.setCount(n);
        }
        // 计算新的库存数量（原库存 +/- 操作数量）
        int num = goods.getCount() + n;
        goods.setCount(num);

        //将record的remark更新到goods
        if (StringUtils.isNotBlank(record.getRemark())) {
            goods.setRemark(record.getRemark());
        }
        goodsService.updateById(goods);

        return recordService.save(record) ? Result.suc() : Result.fail();
    }


    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam queryPageParam) {
        //从请求参数中获取查询条件Map  获取各查询条件参数（如商品名称、商品类型、仓库等）
        HashMap param = queryPageParam.getParam();
        String name = (String) param.get("name");
        String goodsType = (String) param.get("goodstype");
        String storage = (String) param.get("storage");
        String roleId = (String) param.get("roleId");
        String userId = (String) param.get("userId");
        Page<Record> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        // 创建查询条件构造器 设置多表关联条件
        QueryWrapper<Record> queryWrapper = new QueryWrapper();
        queryWrapper.apply(" a.goods=b.id and b.storage=c.id and b.goodsType=d.id ");

        //默认按创建时间降序排序（最新记录在前）
        queryWrapper.orderByDesc("a.createTime");

        if ("2".equals(roleId)) { //普通用户只能查看自己的记录
            queryWrapper.apply(" a.user_id= " + userId);
        }
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            queryWrapper.like("b.name", name);
        }
        if (StringUtils.isNotBlank(goodsType) && !"null".equals(goodsType)) {
            queryWrapper.eq("d.id", goodsType);
        }
        if (StringUtils.isNotBlank(storage) && !"null".equals(storage)) {
            queryWrapper.eq("c.id", storage);
        }

        //调用自定义的分页查询方法
        IPage result = recordService.pageCC(page, queryWrapper);
        return Result.suc(result.getRecords(), result.getTotal());
    }
}
