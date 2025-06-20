package com.wms.controller;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Goods;
import com.wms.entity.Goodstype;
import com.wms.entity.Storage;
import com.wms.entity.User;
import com.wms.service.GoodsService;
import com.wms.service.GoodstypeService;
import com.wms.service.StorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private GoodstypeService goodstypeService;

    @Resource
    private StorageService storageService;

    @PostMapping("/save")
    public Result add(@RequestBody Goods goods) {
        System.out.println("Save goods data: " + goods);
        return goodsService.save(goods) ? Result.suc() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        return goodsService.updateById(goods) ? Result.suc() : Result.fail();
    }

    @GetMapping("/del")
    public Result del(@RequestParam String id) {
        return goodsService.removeById(id) ? Result.suc() : Result.fail();
    }

    @GetMapping("/list")
    public Result list(){
        List list =  goodsService.goodsList();
        return Result.suc(list);
    }

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam queryPageParam) {
        HashMap param = queryPageParam.getParam();
        String name = (String) param.get("name");
        String goodsType = (String) param.get("goodstype");
        String storage = (String) param.get("storage");

        Page<Goods> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            lambdaQueryWrapper.like(Goods::getName, name);
        }
        if (StringUtils.isNotBlank(goodsType) && !"null".equals(goodsType)) {
            lambdaQueryWrapper.eq(Goods::getGoodstype, goodsType);
        }
        if (StringUtils.isNotBlank(storage) && !"null".equals(storage)) {
            lambdaQueryWrapper.eq(Goods::getStorage, storage);
        }

        IPage result = goodsService.pageCC(page, lambdaQueryWrapper);
        goodsService.pageCC(page, lambdaQueryWrapper);

        List<Goods> goodsList = goodsService.lambdaQuery()
                .orderByDesc(Goods::getId)
                .list();

         return Result.suc(result.getRecords(),result.getTotal());
    }


    @GetMapping("/export")
    public void exportGoods(HttpServletResponse response) throws Exception {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<Goods> list = goodsService.goodsList();

        // 获取仓库和分类的映射关系
        Map<Integer, String> storageMap = storageService.getStorageMap();
        Map<Integer, String> goodsTypeMap = goodstypeService.getGoodsTypeMap();

        // 创建用于导出的Map列表
        List<Map<String, Object>> exportList = new ArrayList<>();
        for (Goods item : list) {
            Map<String, Object> goods = new LinkedHashMap<>();
            goods.put("id", item.getId());
            goods.put("name", item.getName());
            goods.put("storage", storageMap.get(item.getStorage())); // 转换为名称
            goods.put("goodstype", goodsTypeMap.get(item.getGoodstype())); // 转换为名称
            goods.put("count", item.getCount());
            goods.put("remark", item.getRemark());
            exportList.add(goods);
        }

        // 设置只导出需要的列（排除原始ID字段）
        writer.addHeaderAlias("id", "序号");
        writer.addHeaderAlias("name", "物品名称");
        writer.addHeaderAlias("storage", "所属仓库");
        writer.addHeaderAlias("goodstype", "所属分类");
        writer.addHeaderAlias("count", "物品数量");
        writer.addHeaderAlias("remark", "备注");

        writer.write(exportList, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode("物品信息", "UTF-8") + ".xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        writer.flush(outputStream, true);
        writer.close();
        outputStream.flush();
        outputStream.close();
    }

    @PostMapping("/import")
    public Result importGoods(@RequestBody MultipartFile file) throws Exception {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<Goods> goodsList = reader.readAll(Goods.class);

        // 转换仓库和分类名称到ID
        for (Goods goods : goodsList) {
            // 根据仓库名称查询仓库ID
            Storage storage = storageService.lambdaQuery()
                    .eq(Storage::getName, goods.getStorageName()) // 假设Goods类中有storageName字段
                    .one();
            if (storage != null) {
                goods.setStorage(storage.getId());
            }

            // 根据分类名称查询分类ID
            Goodstype goodsType = goodstypeService.lambdaQuery()
                    .eq(Goodstype::getName, goods.getGoodsTypeName()) // 假设Goods类中有goodsTypeName字段
                    .one();
            if (goodsType != null) {
                goods.setGoodstype(goodsType.getId());
            }
        }

        // 写入数据到数据库
        goodsService.saveBatch(goodsList);
        return Result.suc();
    }

    @GetMapping("/statistics")
    public Result getGoodsStatistics() {
        // 1. 按仓库统计商品数量
        List<Map<String, Object>> storageStats = goodsService.getGoodsCountByStorage();
        // 2. 按分类统计商品数量
        List<Map<String, Object>> typeStats = goodsService.getGoodsCountByType();
        // 3. 库存预警统计(数量<10的)
        int lowStockCount = goodsService.getLowStockCount(10);
        // 4. 商品总数
        int totalGoods = goodsService.count();
        // 5. 库存总量
        int totalStock = goodsService.getTotalStock();
        // 6. 按物品统计商品数量（新增）
        List<Map<String, Object>> goodsStats = goodsService.getGoodsCountByGoods();
        Map<String, Object> result = new HashMap<>();
        result.put("storageStats", storageStats);
        result.put("typeStats", typeStats);
        result.put("goodsStats", goodsStats);
        result.put("lowStockCount", lowStockCount);
        result.put("totalGoods", totalGoods);
        result.put("totalStock", totalStock);
        return Result.suc(result);


    }
}
